package mtcore.parser.metadata;

import mtcore.parser.block.CBlock;
import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;
import mtcore.parser.file.CFileInfo;

import java.nio.ByteBuffer;

public class CSoundMetadata extends CMetadata {
    ESoundType soundType;
    CFileInfo fileInfo;
    long duration;
    CBlockID dataBlockID;
    short artLength;
    byte[] artData;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.read(bfr);
        soundType = ESoundType.parse(bfr.get());
        fileInfo = new CFileInfo();
        fileInfo.read(bfr);
        duration = bfr.getLong();
        dataBlockID = new CBlockID();
        dataBlockID.read(bfr);
        artLength = bfr.getShort();
        artData = new byte[artLength];
        bfr.get(artData);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.write(bfr);
        bfr.put(soundType.v());
        fileInfo.write(bfr);
        bfr.putLong(duration);
        dataBlockID.write(bfr);
        artLength = (short) artData.length;
        bfr.putShort(artLength);
        bfr.put(artData);
    }

    @Override
    public int getLength() {
        return super.getLength() + Byte.BYTES + fileInfo.getLength() + Long.BYTES + dataBlockID.getLength() + Short.BYTES + artData.length;
    }
}
