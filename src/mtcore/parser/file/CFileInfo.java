package mtcore.parser.file;

import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class CFileInfo implements IAbstractStructure {
    // todo maybe include file properties
    short nameLength;
    byte[] fileName;
    EFileType fileType;
    long fileLength;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        nameLength = bfr.getShort();
        fileName = new byte[nameLength];
        bfr.get(fileName);
        fileType = EFileType.parse(bfr.getShort());
        fileLength = bfr.getLong();
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        nameLength = (short) fileName.length;
        bfr.putShort(nameLength);
        bfr.put(fileName);
        bfr.putShort(fileType.v());
        bfr.putLong(fileLength);
    }

    @Override
    public int getLength() {
        return Short.BYTES + fileName.length + Short.BYTES + Long.BYTES;
    }
}
