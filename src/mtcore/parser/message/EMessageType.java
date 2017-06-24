package mtcore.parser.message;

public enum EMessageType {
    Text((byte) 0x31), Image((byte) 0x32), Video((byte) 0x33), Audio((byte) 0x34), Voice((byte) 0x35), Document((byte) 0x36), WebLink((byte) 0x37), Unknown((byte) 0x30);

    byte unknown_value;
    byte value;

    EMessageType(byte val) {
        for (EMessageType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0xFF;
    }

    public Byte v() { return value; }

    public static EMessageType parse(byte val) {
        EMessageType ret = Unknown;
        for (EMessageType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
