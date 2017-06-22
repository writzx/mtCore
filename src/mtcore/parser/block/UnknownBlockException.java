package mtcore.parser.block;

public class UnknownBlockException extends IllegalArgumentException {
    EBlockType bType;

    public EBlockType getBlockType() { return bType; }

    public UnknownBlockException() {
        bType = EBlockType.Unknown;
        bType.unknown_value = (byte) 0x00;
    }

    public UnknownBlockException(EBlockType bType) {
        this.bType = bType;
    }

    public UnknownBlockException(String s, EBlockType bType) {
        super(s);
        this.bType = bType;
    }

    public UnknownBlockException(String message, Throwable cause, EBlockType bType) {
        super(message, cause);
        this.bType = bType;
    }

    public UnknownBlockException(Throwable cause, EBlockType bType) {
        super(cause);
        this.bType = bType;
    }
}
