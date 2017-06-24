package mtcore.parser.metadata;

public enum EMetadataType {
    Thumbnail((byte) 0x1F),
    Sound((byte) 0x4F),
    File((byte) 0x5F),
    Directory((byte) 0x6F), // planned
    Multipart((byte) 0x9F);

    byte unknown_value;
    byte value;

    EMetadataType(byte val) {
        for (EMetadataType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (byte) 0x9F;
    }

    public Byte v() { return value; }

    public static EMetadataType parse(byte val) {
        EMetadataType ret = File;
        for (EMetadataType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
