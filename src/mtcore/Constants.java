package mtcore;

public class Constants {
    public final static int MAX_DATA_LENGTH = 0x2000;
    public final static int MAX_HEAD_LENGTH = 8;
    public final static int MAX_BLOCK_LENGTH = MAX_DATA_LENGTH + MAX_HEAD_LENGTH;

    public final static int INITIAL_CRC = 0x1337;
}
