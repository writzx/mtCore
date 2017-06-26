package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CDataBlock extends CBlock {
    short dataLength; // less than 0x2000
    byte[] data; // length = dataLength ****

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        dataLength = bfr.getShort();
        if (dataLength > Constants.MAX_DATA_LENGTH || dataLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, dataLength);
        }
        data = new byte[dataLength];
        bfr.get(data);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        dataLength  = (short) data.length;
        if (dataLength > Constants.MAX_DATA_LENGTH || dataLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
        bfr.putShort(dataLength);
        bfr.put(data);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + data.length;
    }
}
