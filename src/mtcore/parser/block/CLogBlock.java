package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CLogBlock extends CBlock {
    short logLength;
    ELogType logType;
    byte[] logData; // length is logLength - [length of logType (1 byte)]

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        logLength = bfr.getShort();
        if (logLength > Constants.MAX_DATA_LENGTH || logLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, logLength);
        }
        logType = ELogType.parse(bfr.get());
        logData = new byte[logLength - Byte.BYTES];
        bfr.get(logData);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        logLength = (short) (logData.length + Byte.BYTES);
        if (logLength > Constants.MAX_DATA_LENGTH || logLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, logLength);
        }
        super.write(bfr);
        bfr.putShort(logLength);
        bfr.put(logType.v());
        bfr.put(logData);
    }

    public enum ELogType {
        Error ((byte) 0x7),
        Warning((byte) 0x8),
        Information((byte) 0x9),
        Debug((byte) 0x10),
        Verbose((byte) 0x11),
        Print((byte) 0x12),
        WTF((byte) 0x13);

        byte unknown_value;
        byte value;

        ELogType(byte val) {
            for (ELogType b : values()) {
                if (b.value == val) {
                    value = val;
                    return;
                }
            }
            unknown_value = val;
            value = (byte) 0x00;
        }

        public byte v() { return value; }

        public static ELogType parse(byte val) {
            ELogType ret = WTF;
            for (ELogType b : values()) {
                if (b.value == val) {
                    ret.value = val;
                    return ret;
                }
            }
            ret.unknown_value = val;
            return ret;
        }
    }
}