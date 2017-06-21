import Parser.Block.CBlock;
import Parser.Block.CPairingBlock;
import Parser.Block.EMethodType;
import Parser.Block.UnknownBlockException;

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

    public void cancel() {
        mCancel = true;
    }

    @Override
    public void run() {
        super.run();
        Selector selector = null;
        try {
            selector = Selector.open();
            mAddresses = getBindAddresses();
            for (SocketAddress address : mAddresses) {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.socket().setReuseAddress(true);
                ssc.socket().bind(address);
                ssc.register(selector, SelectionKey.OP_ACCEPT);
            }

            onThreadStart();

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
                            EAuth auth = checkAuthorization(sc.socket());
                            switch (auth) {
                                case Authorized:
                                    // might be interface call
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.AuthRequested);
                                    requestAuth(sc);
                                    break;
                                case NotAuthorized:
                                    // might be interface call
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.NotAuthorized);
                                    break;
                                case Unauthorized:
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.Unauthorized);
                                    reportUnauthorized(sc.socket());
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
                                    readBuffer(sc, buffer);
                                }
                                it.remove();
                                break;
                            case AuthRequested:
                                if (verifyAuth(sc)) { // reads block and sends to interface to verify
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.Authorized);
                                    respondAuth(sc);
                                }
                                it.remove();
                                break;
                            case NotAuthorized:
                                buffer = onIncomingData(sc);
                                if (buffer != null && CBlock.factory(buffer) instanceof CPairingBlock) {
                                    sc.register(selector, SelectionKey.OP_READ, EAuth.AuthRequested);
                                    readPairBuffer(sc, buffer);

                                }
                                // contains authorization information (authorization value or pairing value)
                                // set the EAuth of the key accordingly in this call
                                // pair_info = Pending (and send pairing response), auth_info = Authorized, else = Unauthorized
                                // todo possibly EAuth will have more values to correctly specify the status (AuthRequested, PairRequested)
                                // todo and we will not change the EAuth attachment for the key here
                                it.remove();
                                break;
                            case RequestingPair:
                                // contains pairing information
                                // todo possibly EAuth will have more values to correctly specify the status (AuthRequested, PairRequested)
                                // todo and we will not change the EAuth attachment for the key here
                                it.remove();
                                break;
                            case PairRequested:
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
            onThreadStop();
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

    ///region todo class methods
    private void readPairBuffer(SocketChannel sc, ByteBuffer bfr) { }
    private boolean verifyAuth(SocketChannel channel) { return false; }
    private void requestAuth(SocketChannel channel) {}
    private void respondAuth(SocketChannel channel) {}
    ///endregion

    ///region todo interface methods probably
    Vector<SocketAddress> getBindAddresses() throws SocketException {

        //TODO Conveniently Interfaces will be determined by looking at DNS-SD broadcast continuously so this function should be a interface function to be handled by native app
        Vector<SocketAddress> ret = new Vector<>();

        ret.add(new InetSocketAddress(9999));

        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                System.out.println(address.toString() + ":9999");
            }
        }

        return ret;
    }
    void onThreadStart() {}
    private void onThreadStop() {}

    EAuth checkAuthorization(Socket sock) { return EAuth.Unauthorized; }
    boolean checkBlacklist(Socket sock) { return false; }
    boolean checkPairing(Socket sock) { return false; }
    void reportUnauthorized(Socket sock) { }
    ///endregion
}
