package mtcore.security;

import mtcore.Log;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AES256 {
    private static final String TAG = "AES256";
    private static final Map<String, AES256> instances = new HashMap<>();

    public static AES256 getInstance(String deviceID) {
        return instances.computeIfAbsent(deviceID, k -> new AES256());
    }

    AES256() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, FixedRandom.instance);
            key = new KeyParameter(generator.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            Log.e(TAG, "AES algorithm was not found in this device.");
        }
    }

    private final BlockCipher AESCipher = new AESEngine();

    private PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(AESCipher, new PKCS7Padding());
    private KeyParameter key;

    public byte[] key() {
        return key.getKey();
    }

    public byte[] encrypt(byte[] input) throws DataLengthException, InvalidCipherTextException {

        return process(input, true);
    }

    public byte[] decrypt(byte[] input) throws DataLengthException, InvalidCipherTextException {
        return process(input, false);
    }

    private byte[] process(byte[] input, boolean encrypt) throws DataLengthException, InvalidCipherTextException {
        cipher.init(encrypt, key);

        byte[] output = new byte[cipher.getOutputSize(input.length)];

        int blockSize = AESCipher.getBlockSize();
        int inOffset, outOffset = 0, remaining = input.length, bytesWritten;

        // process in blocks
        for (inOffset = 0; inOffset < input.length && blockSize <= remaining; inOffset += blockSize) {
            bytesWritten = cipher.processBytes(input, inOffset, blockSize, output, outOffset);
            outOffset += bytesWritten;

            remaining = input.length - inOffset;
        }
        // process remaining
        bytesWritten = cipher.processBytes(input, inOffset, remaining, output, outOffset);
        outOffset += bytesWritten;

        cipher.doFinal(output, outOffset);

        return output;
    }
}
