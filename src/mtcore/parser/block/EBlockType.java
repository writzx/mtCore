package mtcore.parser.block;

public enum EBlockType {
    Authorization((byte) 0xAA),
    Pairing((byte) 0xBB),
    Message((byte) 0xCC),
    Metadata((byte) 0xDD),
    Data((byte) 0xDD),
    Information ((byte) 0xEE),
    Unknown((byte) 0xFF);

    byte unknown_value;
    byte value;

    EBlockType(byte val) {
        for (EBlockType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0xFF;
    }

    public Byte v() { return value; }

    public static EBlockType parse(byte val) {
        EBlockType ret = Unknown;
        for (EBlockType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
