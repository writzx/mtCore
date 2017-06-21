package Parser.Block;

import java.nio.ByteBuffer;

public class CMessageBlock extends CBlock {
    short messageLength; // max value = 0x2000 (as of now)
    byte[] message;      // length = messageLength

    @Override
    public void read(ByteBuffer bfr) {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) {
        super.write(bfr);
    }
}
