package mtcore.parser.block;

import mtcore.parser.IAbstractStructure;

import java.nio.ByteBuffer;

public class CBlockID implements IAbstractStructure {
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

    @Override
    public int getLength() {
        return Byte.BYTES + Byte.BYTES;
    }

}
