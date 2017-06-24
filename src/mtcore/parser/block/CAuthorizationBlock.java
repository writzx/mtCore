package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CAuthorizationBlock extends CBlock {
    public short authLength; // less than 0x2000

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        authLength = bfr.getShort();
        if (authLength > Constants.MAX_DATA_LENGTH || authLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, authLength);
        }
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        if (authLength > Constants.MAX_DATA_LENGTH || authLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
        bfr.putShort(authLength);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES;
    }

    private static CAuthorizationBlock factory(EMethodType method) throws CorruptedBlockException {
        switch (method) {
            case Request: return new CAuthRequest();
            case Response: return new CAuthResponse();
            case Unknown:
            default: throw new CorruptedBlockException("Corrupted block", method, (short) 0);
        }
    }

    public static CAuthorizationBlock factory(ByteBuffer bfr) throws CorruptedBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EMethodType.parse(b));
    }

    public static class CAuthRequest extends CAuthorizationBlock {
        public byte[] reserved; // random data, length = authLength; mostly empty

        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
            reserved = new byte[authLength];
            bfr.get(reserved);
        }

        @Override
        public void write(ByteBuffer bfr) throws CorruptedBlockException {
            authLength = (short) reserved.length;
            super.write(bfr);
            bfr.put(reserved);
        }

        @Override
        public int getLength() {
            return super.getLength() + reserved.length;
        }
    }

    public static class CAuthResponse extends CAuthorizationBlock {
        public byte[] authData; // contains encrypted ip:port address (remove leading '/')

        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
            authData = new byte[authLength];
            bfr.get(authData);
        }

        @Override
        public void write(ByteBuffer bfr) throws CorruptedBlockException {
            authLength = (short) authData.length;
            super.write(bfr);
            bfr.put(authData);
        }

        @Override
        public int getLength() {
            return super.getLength() + authData.length;
        }
    }
}
