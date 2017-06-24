package mtcore.parser.message;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class CLink implements IAbstractStructure {
    ELinkType linkType;
    CBlockID linkedBlockID;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {

    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {

    }

    @Override
    public int getLength() {
        return Byte.BYTES + linkedBlockID.getLength();
    }
}
