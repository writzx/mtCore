package Parser.Block;

import java.nio.ByteBuffer;

public class CAuthorizationBlock extends CBlock {
    short authLength; // less than 0x2000

    @Override
    public void read(ByteBuffer bfr) {
        super.read(bfr);
        authLength = bfr.getShort(); // todo check
    }

    @Override
    public void write(ByteBuffer bfr) {
        super.write(bfr);
        bfr.putShort(authLength); // todo check
    }

    private static CAuthorizationBlock factory(EMethodType method) throws CorruptedBlockException {
        switch (method) {
            case Request: return new CAuthRequestBlock();
            case Response: return new CAuthResponseBlock();
            case Unknown:
            default: throw new CorruptedBlockException("Corrupted Block", method, (short) 0);
        }
    }

    public static CAuthorizationBlock factory(ByteBuffer bfr) throws CorruptedBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EMethodType.parse(b));
    }

    public static class CAuthRequestBlock extends CAuthorizationBlock {
        byte[] reserved; // random data, length = authLength;

        @Override
        public void read(ByteBuffer bfr) {
            super.read(bfr);
            reserved = new byte[authLength];
            bfr.get(reserved); // todo check
        }

        @Override
        public void write(ByteBuffer bfr) {
            super.write(bfr);
            bfr.put(reserved); // todo check
        }
    }

    public static class CAuthResponseBlock extends CAuthorizationBlock {
        byte[] authData; // dummy for now (will have specific format later), length = authLength;

        @Override
        public void read(ByteBuffer bfr) {
            super.read(bfr);
            authData = new byte[authLength];
            bfr.get(authData); // todo check
        }

        @Override
        public void write(ByteBuffer bfr) {
            super.write(bfr);
            bfr.put(authData); // todo check
        }
    }
}
