package mtcore.parser.metadata;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class CPart implements IAbstractStructure {
    long start;
    long end;
    CBlockID partID;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {

    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {

    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getMaxDataLength() {
        return 0;
    }
}
