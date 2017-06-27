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

    public static CMetadata factory(ByteBuffer bfr) {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EMetadataType.parse(b));
    }

    public static CMetadata factory(EMetadataType meType) {
        switch (meType) {
            case Thumbnail: return new CThumbnailMetadata();
            case Sound: return new CSoundMetadata();
            case Multipart: return new CMultipartMetadata();
            case File: default: return new CFileMetadata();
        }
    }
}
