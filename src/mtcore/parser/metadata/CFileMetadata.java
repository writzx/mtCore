package mtcore.parser.metadata;

import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;
import mtcore.parser.file.CFileInfo;

import java.nio.ByteBuffer;

public class CFileMetadata extends CMetadata {
    CFileInfo fileInfo;
    CBlockID dataBlockID;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.read(bfr);
        fileInfo = new CFileInfo();
        fileInfo.read(bfr);
        dataBlockID = new CBlockID();
        dataBlockID.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.write(bfr);
        fileInfo.write(bfr);
        dataBlockID.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + fileInfo.getLength() + dataBlockID.getLength();
    }
}
