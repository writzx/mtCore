package mtcore.parser.block;

public enum EMethodType {
    Request((byte) 0x55),
    Response((byte) 0x66),
    Unknown((byte) 0x44);

    byte unknown_value;
    byte value;

    EMethodType(byte val) {
        for (EMethodType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0x44;
    }

    public byte v() { return value; }

    public static EMethodType parse(byte val) {
        EMethodType ret = Unknown;
        for (EMethodType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
