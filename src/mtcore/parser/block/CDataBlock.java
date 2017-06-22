package mtcore.parser.block;

import java.nio.ByteBuffer;

public class CDataBlock extends CBlock {
    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
    }
}
