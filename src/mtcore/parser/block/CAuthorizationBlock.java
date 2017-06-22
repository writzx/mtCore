package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CAuthorizationBlock extends CBlock {
    short authLength; // less than 0x2000

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        authLength = bfr.getShort();
        if (authLength > Constants.MAX_DATA_SIZE || authLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, authLength);
        }
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
        if (authLength > 0x2000 || authLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        bfr.putShort(authLength);
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
        byte[] reserved; // random data, length = authLength;

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
    }

    public static class CAuthResponse extends CAuthorizationBlock {
        byte[] authData; // dummy for now (will have specific format later), length = authLength;

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
    }
}
