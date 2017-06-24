package mtcore.parser.metadata;

import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;
import mtcore.parser.file.FileInfo;

import java.nio.ByteBuffer;

public class CThumbnailMetadata extends CMetadata {
    EThumbnailType thumbType;
    FileInfo fileInfo;
    long duration;
    CBlockID dataBlockID;
    short thumbLength;
    byte[] thumbData;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength();
    }

    @Override
    public int getMaxDataLength() {
        return 0;
    }
}
