package mtcore.parser.block;

import java.nio.ByteBuffer;

public class CMessageBlock extends CBlock {
    short messageLength; // max value = 0x2000 (as of now)
    byte[] message;      // length = messageLength

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
    }
}
