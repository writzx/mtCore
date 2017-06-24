package mtcore.parser.file;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class FileInfo implements IAbstractStructure {
    byte[] fileName;
    EFileType fileType;
    long fileLength;

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
