package mtcore.security;

import mtcore.Log;
import mtcore.parser.message.CMessage;
import mtcore.parser.metadata.CMetadata;
import mtcore.parser.metadata.EMetadataType;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.nio.ByteBuffer;

public class Decrypt {
    public static final String TAG = "Decrypt";

    public static CMessage message(byte[] messageBytes, String deviceID) {
        try {
            byte[] decrypted = AES256.getInstance(deviceID).decrypt(messageBytes);
            ByteBuffer buffer = ByteBuffer.wrap(decrypted);

            CMessage msg = new CMessage();

            msg.read(buffer);

            return msg;
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }

    public static CMetadata metadata(byte[] metadataBytes, String deviceID, EMetadataType mType) {
        try {
            byte[] decrypted = AES256.getInstance(deviceID).decrypt(metadataBytes);
            ByteBuffer buffer = ByteBuffer.wrap(decrypted);

            CMetadata meta = CMetadata.factory(mType);
            meta.read(buffer);

            return meta;
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }

    public static byte[] data(byte[] dataBytes, String deviceID) {
        try {
            return AES256.getInstance(deviceID).decrypt(dataBytes);
        } catch (InvalidCipherTextException e) {
            Log.wtf(TAG, "InvalidCipherTextException");
        }
        return null;
    }
}
