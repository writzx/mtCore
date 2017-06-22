package Parser.Block;

public class CorruptedBlockException extends IllegalArgumentException {
    EMethodType methodType;
    short length;

    public CorruptedBlockException() {
        methodType = EMethodType.Unknown;
        methodType.unknown_value = (byte) 0x00;
        length = 0;
    }

    public CorruptedBlockException(EMethodType methodType, short length) {
        this.methodType = methodType;
        this.length = length;
    }

    public CorruptedBlockException(String s, EMethodType methodType, short length) {
        super(s);
        this.methodType = methodType;
        this.length = length;
    }

    public CorruptedBlockException(String message, Throwable cause, EMethodType methodType, short length) {
        super(message, cause);
        this.methodType = methodType;
        this.length = length;
    }

    public CorruptedBlockException(Throwable cause, EMethodType methodType, short length) {
        super(cause);
        this.methodType = methodType;
        this.length = length;
    }
}
