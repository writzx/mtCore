package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CPairingBlock extends CBlock {
    short pairLength; // both request and response has to have correct length
                      // length will be according to their inner format


    private static CPairingBlock factory(EMethodType method) throws CorruptedBlockException {
        switch (method) {
            case Request: return new CPairRequest();
            case Response: return new CPairResponse();
            case Unknown:
            default: throw new CorruptedBlockException("Corrupted block", method, (short) 0);
        }
    }

    public static CPairingBlock factory(ByteBuffer bfr) throws CorruptedBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EMethodType.parse(b));
    }

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        if (pairLength > Constants.MAX_DATA_SIZE || pairLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, pairLength);
        }
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
        if (pairLength > 0x2000 || pairLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, pairLength);
        }
        bfr.putShort(pairLength);
    }

    public static class CPairRequest extends CPairingBlock {
        byte[] pubkeyData; // encrypted with passcode which is to be obtained from remote user

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
    }

    public static class CPairResponse extends CPairingBlock {
        short codeLength;
        short pubkeyLength;
        byte[] codeData;        // encrypted passcode
        byte[] pubkeyData;      // encrypted public key

        @Override
        public void read(ByteBuffer bfr) throws CorruptedBlockException {
            super.read(bfr);
            codeLength = bfr.getShort();
            pubkeyLength = bfr.getShort();
            short tmpLen = (short) (codeLength + pubkeyLength + (2 * 2)); // two short length
            if (tmpLen > pairLength && (tmpLen > Constants.MAX_DATA_SIZE || tmpLen > bfr.remaining())) {
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
    }
}
