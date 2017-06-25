package mtcore.parser.metadata;

import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;
import mtcore.parser.file.CFileInfo;

import java.nio.ByteBuffer;

public class CThumbnailMetadata extends CMetadata {
    EThumbnailType thumbType;
    CFileInfo fileInfo;
    long duration;
    CBlockID dataBlockID;
    short thumbLength;
    byte[] thumbData;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.read(bfr);
        thumbType = EThumbnailType.parse(bfr.get());
        fileInfo = new CFileInfo();
        fileInfo.read(bfr);
        duration = bfr.getLong();
        dataBlockID = new CBlockID();
        dataBlockID.read(bfr);
        thumbLength = bfr.getShort();
        thumbData = new byte[thumbLength];
        bfr.get(thumbData);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.write(bfr);
        bfr.put(thumbType.v());
        fileInfo.write(bfr);
        bfr.putLong(duration);
        dataBlockID.write(bfr);
        thumbLength = (short) thumbData.length;
        bfr.putShort(thumbLength);
        bfr.put(thumbData);
    }

    @Override
    public int getLength() {
        return super.getLength() + Byte.BYTES + fileInfo.getLength() + Long.BYTES + dataBlockID.getLength() + Short.BYTES + thumbData.length;
    }
}
