package mtcore.parser.metadata;

public enum ESoundType {
    Audio((byte) 0x91), Voice((byte) 0x92), Unknown ((byte) 0x99);

    byte unknown_value;
    byte value;

    ESoundType(byte val) {
        for (ESoundType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0x99;
    }

    public byte v() { return value; }

    public static ESoundType parse(byte val) {
        ESoundType ret = Unknown;
        for (ESoundType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
