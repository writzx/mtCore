package mtcore.parser.block;

import java.nio.ByteBuffer;

public class CBlockID {
    byte group;
    byte code;

    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        group = bfr.get();
        code = bfr.get();
    }
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        bfr.put(group);
        bfr.put(code);
    }
}
