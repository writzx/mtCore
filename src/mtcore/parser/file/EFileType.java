package mtcore.parser.file;

import mtcore.parser.metadata.ESoundType;

public enum EFileType {
    // todo log all the common file formats
    // todo create FileType enum to map these to extensions
    Text((short) 0x0001), MicrosoftWordDocument((short) 0x0002), Binary((short) 0xFFFF);
    short unknown_value;
    short value;

    EFileType(short val) {
        for (EFileType b : values()) {
            if (b.value == val) {
                value = val;
                return;
            }
        }
        unknown_value = val;
        value = (short) 0xFFFF;
    }

    public short v() { return value; }

    public static EFileType parse(short val) {
        EFileType ret = Binary;
        for (EFileType b : values()) {
            if (b.value == val) {
                ret.value = val;
                return ret;
            }
        }
        ret.unknown_value = val;
        return ret;
    }
}
