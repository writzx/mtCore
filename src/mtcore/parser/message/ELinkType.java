package mtcore.parser.message;

public enum ELinkType {
    Reply((byte) 0x69),
    Attachment((byte) 0x70),
    None((byte) 0x00);

    byte unknown_value;
    byte value;

    ELinkType(byte val) {
        for (ELinkType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0x00;
    }

    public byte v() { return value; }

    public static ELinkType parse(byte val) {
        ELinkType ret = None;
        for (ELinkType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
