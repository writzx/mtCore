package mtcore.parser.block;

import java.nio.ByteBuffer;

import mtcore.Constants;

public class CPairingBlock extends CBlock {
    public short pairLength; // both request and response has to have correct length
                      // length will be according to their inner format

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES;
    }

    private static CPairingBlock factory(EMethodType method) throws CorruptedBlockException {
        switch (method) {
            case Request: return new CPairRequest();
            case Response: return new CPairResponse();
            case Unknown:
            default: throw new CorruptedBlockException("Corrupted block", method, (short) 0);
        }
    }

    public static CPairingBlock factory(ByteBuffer bfr) throws CorruptedBlockException {
        return factory(EMethodType.parse(bfr.get()));
    }

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        pairLength = bfr.getShort();
        if (pairLength > Constants.MAX_DATA_LENGTH || pairLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, pairLength);
        }
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        if (pairLength > Constants.MAX_DATA_LENGTH || pairLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, pairLength);
        }
        super.write(bfr);
        bfr.putShort(pairLength);
    }

    public static class CPairRequest extends CPairingBlock {
        public byte[] pubkeyData; // encrypted with passcode which is to be obtained from remote user ****

        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
            pubkeyData = new byte[pairLength];
            bfr.get(pubkeyData);
        }

        @Override
        public void write(ByteBuffer bfr) throws CorruptedBlockException {
            pairLength = (short) pubkeyData.length; // here assign the value of pairLength correctly before calling super
            super.write(bfr);
            bfr.put(pubkeyData);
        }

        @Override
        public int getLength() {
            return super.getLength() + pubkeyData.length;
        }
    }

    public static class CPairResponse extends CPairingBlock {
        public short codeLength;
        public short pubkeyLength;
        public byte[] codeData;        // encrypted passcode ****
        public byte[] pubkeyData;      // encrypted public key ****

        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
            codeLength = bfr.getShort();
            pubkeyLength = bfr.getShort();
            short tmpLen = (short) (codeLength + pubkeyLength + (2 * 2)); // two short length
            if (tmpLen > pairLength && (tmpLen > Constants.MAX_DATA_LENGTH || tmpLen > bfr.remaining())) {
                throw new CorruptedBlockException(method, tmpLen);
            }
            codeData = new byte[codeLength];
            bfr.get(codeData);
            pubkeyData = new byte[pubkeyLength];
            bfr.get(pubkeyData);
            // remove the padding if pairLength is greater than actual block length
            if (pairLength > tmpLen) {
                byte[] padding = new byte[pairLength - tmpLen];
                bfr.get(padding);
            }
            // set actual pairLength
            pairLength = tmpLen;
        }

        @Override
        public void write(ByteBuffer bfr) throws CorruptedBlockException {
            codeLength = (short) codeData.length;
            pubkeyLength = (short) pubkeyData.length;
            pairLength = (short) (codeLength + pubkeyLength + (2 * 2)); // two short length
            super.write(bfr);
            bfr.putShort(codeLength);
            bfr.putShort(pubkeyLength);
            bfr.put(codeData);
            bfr.put(pubkeyData);
        }

        @Override
        public int getLength() {
            return super.getLength() + Short.BYTES + Short.BYTES + codeData.length + pubkeyData.length;
        }
    }
}
