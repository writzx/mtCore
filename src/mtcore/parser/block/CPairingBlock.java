package mtcore.parser.block;

import java.nio.ByteBuffer;

public class CPairingBlock extends CBlock {
    short pairLength; // both request and response has to have correct length
                      // length will be according to their inner format


    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
    }

    public static class CPairRequest extends CPairingBlock {
        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
        }
    }
}
