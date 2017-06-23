package mtcore;

import mtcore.parser.block.*;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class IncomingSocketThread extends Thread {
    private String TAG = "IncomingSocketThread: ";

    private Vector<SocketAddress> mAddresses;

    private boolean mCancel = false;

    private HashMap<Socket, Boolean> mDiscardClients = new HashMap<>();
    private HashMap<Socket, ByteBuffer> mClientBuffers = new HashMap<>();

    private Selector selector = null;

    public void cancel() {
        mCancel = true;
    }

    IIncomingSocketListener mSocketListener;

    @Override
    public void run() {
        super.run();
        try {
            selector = Selector.open();
            mAddresses = mSocketListener.getBindAddresses();
            for (SocketAddress address : mAddresses) {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.socket().setReuseAddress(true);
                ssc.socket().bind(address);
                ssc.register(selector, SelectionKey.OP_ACCEPT);
            }

            mSocketListener.onThreadStart();

            Iterator<SelectionKey> it;
            while (!mCancel) {
                selector.select();
                while ((it = selector.selectedKeys().iterator()).hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable() && key.channel() instanceof ServerSocketChannel) {
                        // accept all connection and register as NotAuthorized
                        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
                        if (sc != null) {
                            sc.configureBlocking(false);
                            sc.socket().setTcpNoDelay(true);
                            // check auth status using DNS-SD id
                            EAuth auth = mSocketListener.checkAuthorization(sc);
                            switch (auth) {
                                case Authorized:
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.AuthRequested);
                                    requestAuth(sc);
                                    break;
                                case NotAuthorized:
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.NotAuthorized);
                                    break;
                                case Unauthorized:
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.Unauthorized);
                                    mSocketListener.reportUnauthorized(sc);
                                    break;
                                default:
                                    // just accept the connection, don't change the auth
                                    sc.register(selector, SelectionKey.OP_READ, auth);
                                    break;
                            }
                        }
                        it.remove();
                    }
                    if (key.isReadable() && key.channel() instanceof SocketChannel) {
                        // handle incoming data
                        SocketChannel sc = (SocketChannel) key.channel();
                        switch ((EAuth) key.attachment()) {
                            case Authorized:
                                ByteBuffer buffer = onIncomingData(sc);
                                if (buffer != null) {
                                    CBlock block = CBlock.factory(buffer);
                                    if (block instanceof CAuthorizationBlock.CAuthRequest) {
                                        Log.w(TAG, "Received authorization request from authorized client! Responded!");
                                        sc.register(selector, SelectionKey.OP_READ, EAuth.Authorizing);
                                        respondAuth(sc);
                                    } else if (block instanceof CAuthorizationBlock.CAuthResponse) {
                                        block.read(buffer);
                                        if (mSocketListener.verifyAuth(sc, (CAuthorizationBlock.CAuthResponse) block)) {
                                            Log.w(TAG, "Received authorization response from authorized client! Verified auth!");
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.Authorized);
                                        } else {
                                            Log.w(TAG, "Received authorization response could not be verified! Requesting for new authorization data!");
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.AuthRequested);
                                            requestAuth(sc);
                                        }
                                    } else if (block instanceof CPairingBlock.CPairRequest) {
                                        // refresh the connection auth and pair, ignore local authorization
                                        block.read(buffer);
                                        if (mSocketListener.handlePairRequest(sc, (CPairingBlock.CPairRequest) block)) {
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.PairRequested);
                                        } else {
                                            Log.w(TAG, "Received pairing information is not in correct format!");
                                            // close the channel
                                            sc.close();
                                        }
                                    } else if (block instanceof CPairingBlock.CPairResponse) {
                                        Log.e(TAG, "Illegal response from authorized client! [Type: CPairingBlock.CPairResponse], buffer was dismissed!");
                                    } else {
                                        readBuffer(sc, buffer);
                                    }
                                }
                                it.remove();
                                break;
                            case AuthRequested:
                                buffer = onIncomingData(sc);
                                if (buffer != null) {
                                    CBlock block = CBlock.factory(buffer);
                                    if (block instanceof CAuthorizationBlock.CAuthResponse) {
                                        block.read(buffer);
                                        if (mSocketListener.verifyAuth(sc, (CAuthorizationBlock.CAuthResponse) block)) { // reads block and sends to interface to verify
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.Authorized);
                                            respondAuth(sc);
                                        } else {
                                            Log.w(TAG, "Authorization response could not be verified!");
                                        }
                                    }
                                }
                                it.remove();
                                break;
                            case NotAuthorized:
                                buffer = onIncomingData(sc);
                                if (buffer != null) {
                                    CBlock block = CBlock.factory(buffer);
                                    if (block instanceof CPairingBlock.CPairRequest) {
                                        block.read(buffer);
                                        if (mSocketListener.handlePairRequest(sc, (CPairingBlock.CPairRequest) block)) {
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.AuthRequested);
                                        } else {
                                            Log.w(TAG, "Received pairing information is not in correct format!");
                                            // close the channel
                                            sc.close();
                                        }
                                    }
                                }
                                it.remove();
                                break;
                            case PairRequested:
                                buffer = onIncomingData(sc);
                                if (buffer!= null) {
                                    CBlock block = CBlock.factory(buffer);
                                    if (block instanceof CPairingBlock.CPairResponse) {
                                        // and read the encrypted remote_public_key and decrypt it
                                        if (mSocketListener.verifyPairResponse(sc, (CPairingBlock.CPairResponse) block)) { // decrypt the passcode bytes using local_private_key
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.Authorizing);
                                            respondAuth(sc);
                                        } else {
                                            Log.w(TAG, "Pairing information could not be verified!");
                                            // close the channel
                                            sc.close();
                                        }
                                    }
                                }
                                it.remove();
                                break;
                            case Authorizing:
                                buffer = onIncomingData(sc);
                                if (buffer != null) {
                                    CBlock block = CBlock.factory(buffer);
                                    if (block instanceof CAuthorizationBlock.CAuthResponse) {
                                        block.read(buffer);
                                        if (mSocketListener.verifyAuth(sc, (CAuthorizationBlock.CAuthResponse) block)) {
                                            sc.register(selector, SelectionKey.OP_READ, EAuth.Authorized);
                                        }
                                    }
                                }
                                it.remove();
                                break;
                            case Unauthorized:
                            default:
                                // send unauthorized message
                                // disconnect the channel
                                it.remove();
                                break;
                        }
                    }
                }
                it = null;
            }
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
            // most common errors (we do not throw these exceptions because they are irrelevant in our case)
            if (e.getClass() == BindException.class && e.getMessage().contains("EADDRINUSE")) {
                // address in use
            } else if (e.getClass() == BindException.class && e.getMessage().contains("EACCES") ) {
                // socket permission denied
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (selector != null) {
                // properly close the selector
                for (SelectionKey key : selector.keys()) {
                    try { key.channel().close(); } catch (IOException ignored) {}
                }
                try { selector.close(); } catch (IOException ignored) {}
            }
            mSocketListener.onThreadStop();
        }
    }

    private ByteBuffer onIncomingData(SocketChannel sc) {
        ByteBuffer buffer = mClientBuffers.get(sc.socket());
        if (buffer == null) {
            // allocate new buffer
            ByteBuffer newBlock = ByteBuffer.allocate(Constants.MAX_BLOCK_SIZE);
            mClientBuffers.put(sc.socket(), newBlock);
            mDiscardClients.put(sc.socket(), false);
            buffer = newBlock;
        }

        int bytesRead;

        try {
            bytesRead = sc.read(buffer);
        } catch (IOException e) {
            try { sc.close(); } catch (IOException ignored) {}
            return null;
        }
        if (bytesRead == -1) {
            try { sc.close(); } catch (IOException ignored) {}
            return null;
        }
        return  buffer;
    }

    private void readBuffer(SocketChannel sc, ByteBuffer buffer) {
        boolean discard = mDiscardClients.get(sc.socket());

        try {
            CBlock block = CBlock.factory(buffer); // UnknownBlockException might be thrown here
            block.read(buffer);                      // or here depending on data size
            if (discard) {
                mDiscardClients.put(sc.socket(), false);
            }
            while (buffer.hasRemaining()) {
                block = CBlock.factory(buffer); // UnknownBlockException might be thrown here
                block.read(buffer);               // or here depending on data size

                // interface call here to deliver the block and the socket to the block decoder thread
            }
            if (!buffer.hasRemaining()) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    block = CBlock.factory(buffer); // UnknownBlockException might be thrown here
                    block.read(buffer);               // or here depending on data size

                    // interface call here to deliver the block and the socket to the block decoder thread
                }
                buffer.clear();
                mDiscardClients.put(sc.socket(), true);
            }
        } catch (UnknownBlockException ube) {
            Log.wtf(TAG, ube.getMessage() + "[TypeID: 0x" + String.format("%02X ", ube.getBlockType().v()) + "]");
            // an unknown block always contains either MAX_BLOCK_LENGTH or buffer.remaining() number of bytes
            // an unknown block will not be read after identification which is done while initializing the block
            // a known block can still be unknown if the header is not defined correctly (wrong header type, data length or crc)
            // in that case it will be called Corrupted and not handled while reading blocks unless the error is due to wrong data length
            buffer.clear();
            // might want to add an interface connection here
            return;
        }
    }

    private void requestAuth(SocketChannel channel) {
        // generate auth request block
        CAuthorizationBlock.CAuthRequest auth_block = new CAuthorizationBlock.CAuthRequest();
        auth_block.blockType = EBlockType.Authorization;
        auth_block.method = EMethodType.Request;

        // auth request block contains no data
        auth_block.authLength = 0x00;
        auth_block.reserved = new byte[0];

        auth_block.id = mSocketListener.generateBlockID(channel, auth_block);
        auth_block.checksum = mSocketListener.generateChecksum(auth_block);

        mSocketListener.sendBlock(channel, auth_block);
    }

    private void respondAuth(SocketChannel channel) {
        CAuthorizationBlock.CAuthResponse auth_block = new CAuthorizationBlock.CAuthResponse();
        auth_block.blockType = EBlockType.Authorization;
        auth_block.method = EMethodType.Response;

        byte[] data = mSocketListener.getAuthData(channel);

        auth_block.authLength = (short) data.length;
        auth_block.authData = data;

        auth_block.id = mSocketListener.generateBlockID(channel, auth_block);
        auth_block.checksum = mSocketListener.generateChecksum(auth_block);

        mSocketListener.sendBlock(channel, auth_block);
    }
}