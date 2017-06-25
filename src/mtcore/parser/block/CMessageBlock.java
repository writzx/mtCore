package mtcore.parser.block;

import mtcore.Constants;
import mtcore.parser.message.CMessage;

import java.nio.ByteBuffer;

public class CMessageBlock extends CBlock {
    short messageBlockLength;
    CMessage message;

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        messageBlockLength = bfr.getShort();
        if (messageBlockLength > Constants.MAX_DATA_LENGTH || messageBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, messageBlockLength);
        }
        message = new CMessage();
        message.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        messageBlockLength = (short) message.getLength();
        if (messageBlockLength > Constants.MAX_DATA_LENGTH || messageBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
        bfr.putShort(messageBlockLength);
        message.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + message.getLength();
    }
}
