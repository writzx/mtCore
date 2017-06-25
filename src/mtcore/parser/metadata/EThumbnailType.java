package mtcore.parser.metadata;

public enum EThumbnailType {
    Video((byte) 0x1), Animated ((byte) 0x2), Image((byte) 0x3), Document((byte) 0x4), Unknown ((byte) 0x9);

    byte unknown_value;
    byte value;

    EThumbnailType(byte val) {
        for (EThumbnailType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0x9;
    }

    public Byte v() { return value; }

    public static EThumbnailType parse(byte val) {
        EThumbnailType ret = Unknown;
        for (EThumbnailType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
