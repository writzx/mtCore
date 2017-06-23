package mtcore;

import mtcore.parser.block.CAuthorizationBlock;
import mtcore.parser.block.CBlock;
import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CPairingBlock;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Vector;

interface IIncomingSocketListener {
    Vector<SocketAddress> getBindAddresses() throws SocketException;

    void onThreadStart();
    void onThreadStop();

    // boolean checkBlacklist(Socket sock);
    // boolean checkPairing(Socket sock);
    EAuth checkAuthorization(SocketChannel sc);
    void reportUnauthorized(SocketChannel sc);
    boolean verifyAuth(SocketChannel sc, CAuthorizationBlock.CAuthResponse block);

    boolean handlePairRequest(SocketChannel sc, CPairingBlock.CPairRequest block);
    boolean verifyPairResponse(SocketChannel sc, CPairingBlock.CPairResponse block);

    byte[] getAuthData(SocketChannel sc);
    CBlockID generateBlockID(SocketChannel sc, CBlock block);
    short generateChecksum(CBlock block);

    void sendBlock(SocketChannel sc, CBlock block);
}
