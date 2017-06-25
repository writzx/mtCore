package mtcore.parser.metadata;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class CPart implements IAbstractStructure {
    long offset;
    short length;
    CBlockID partID;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        offset = bfr.getLong();
        length = bfr.getShort();
        partID = new CBlockID();
        partID.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        bfr.putLong(offset);
        bfr.putShort(length);
        partID.write(bfr);
    }

    @Override
    public int getLength() {
        return Long.BYTES + Long.BYTES + partID.getLength();
    }
}
