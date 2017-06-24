package mtcore.parser.metadata;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public abstract class CMetadata implements IAbstractStructure {
    EMetadataType mType;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        mType = EMetadataType.parse(bfr.get());
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        bfr.put(mType.v());
    }

    @Override
    public int getLength() {
        return Byte.BYTES;
    }
}
