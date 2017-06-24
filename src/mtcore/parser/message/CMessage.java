package mtcore.parser.message;

import mtcore.Constants;
import mtcore.parser.IAbstractStructure;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public class CMessage implements IAbstractStructure {
    CLink link; // linked block description
    long timestamp; // message send time
    short messageLength;
    byte[] message;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        link = new CLink();
        link.read(bfr);
        timestamp = bfr.getLong();
        messageLength = bfr.getShort();
        message = new byte[messageLength];
        bfr.get(message);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        link.write(bfr);
        bfr.putLong(timestamp);
        bfr.putShort(messageLength);
        bfr.put(message);
    }

    @Override
    public int getLength() {
        return Byte.BYTES + link.getLength() + Long.BYTES + Short.BYTES + message.length;
    }

    @Override
    public int getMaxDataLength() {
        return Constants.MAX_DATA_LENGTH - (Byte.BYTES + link.getLength() + Long.BYTES + Short.BYTES);
    }
}
