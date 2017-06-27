package mtcore.security;

import mtcore.Constants;
import mtcore.Log;
import mtcore.parser.message.CMessage;
import mtcore.parser.metadata.CMetadata;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.nio.ByteBuffer;

public class Encrypt {
    public static final String TAG = "Encrypt";

    public static byte[] message(CMessage message, String deviceID) {
        ByteBuffer bfr = ByteBuffer.allocate(Constants.MAX_DATA_LENGTH);
        message.write(bfr);
        try {
            return AES256.getInstance(deviceID).encrypt(bfr.array());
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }

    public static byte[] metadata(CMetadata metadata, String deviceID) {
        ByteBuffer bfr = ByteBuffer.allocate(Constants.MAX_DATA_LENGTH);
        metadata.write(bfr);
        try {
            return AES256.getInstance(deviceID).encrypt(bfr.array());
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }

    public static byte[] data(byte[] dataBytes, String deviceID) {
        try {
            return AES256.getInstance(deviceID).encrypt(dataBytes);
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }
}
