package mtcore.parser.block;

import mtcore.Constants;
import mtcore.parser.message.CMessage;

import java.nio.ByteBuffer;

public class CMessageBlock extends CBlock {
    short messageLength;
    CMessage message;
    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        messageLength = bfr.getShort();
        if (messageLength > Constants.MAX_DATA_LENGTH || messageLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, messageLength);
        }
        message.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        messageLength = (short) message.getLength();
        if (messageLength > Constants.MAX_DATA_LENGTH || messageLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + 2 + message.getLength();
    }
}
