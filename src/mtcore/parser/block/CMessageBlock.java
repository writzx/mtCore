package mtcore.parser.block;

import mtcore.Constants;
import mtcore.parser.message.CMessage;

import java.nio.ByteBuffer;

public class CMessageBlock extends CBlock {
    short messageBlockLength;
    byte[] messageBlock;
    // CMessage message;

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        messageBlockLength = bfr.getShort();
        if (messageBlockLength > Constants.MAX_DATA_LENGTH || messageBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, messageBlockLength);
        }
        messageBlock = new byte[messageBlockLength];
        bfr.get(messageBlock);
        // message = new CMessage();
        // message.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        messageBlockLength = (short) messageBlock.length;
        if (messageBlockLength > Constants.MAX_DATA_LENGTH || messageBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
        bfr.putShort(messageBlockLength);
        bfr.put(messageBlock);
        // message.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + messageBlock.length;
    }
}
