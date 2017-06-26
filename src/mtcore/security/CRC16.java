package mtcore.security;

import mtcore.Constants;

public class CRC16 {
    public static final CRC16 instance = new CRC16();
    private static final int POLYNOMIAL = 0x8005;

    private short[] crcTable;

    private CRC16() {
        crcTable = genCrc16TableLsbFirst(POLYNOMIAL);
    }

    public static int calculate(byte[] data) {
        return instance.calculateCrcLsbFirst(data, Constants.INITIAL_CRC); // todo decide intial value
    }

    private int calculateCrcLsbFirst(byte[] data, int initialCrcValue) {
        int crc = initialCrcValue;
        for (int p = 0; p < data.length; p++) {
            crc = (crc >> 8) ^ (crcTable[(crc & 0xFF) ^ (data[p] & 0xFF)] & 0xFFFF);
        }
        return crc;
    }

    private static short[] genCrc16TableLsbFirst(int poly) {
        short[] table = new short[256];
        for (int x = 0; x < 256; x++) {
            int w = x;
            for (int i = 0; i < 8; i++) {
                if ((w & 1) != 0) {
                    w = (w >> 1) ^ poly;
                } else {
                    w = w >> 1;
                }
            }
            table[x] = (short) w;
        }
        return table;
    }
}
