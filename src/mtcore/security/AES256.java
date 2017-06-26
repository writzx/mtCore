package mtcore.security;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import java.util.HashMap;
import java.util.Map;

public class AES256 {
    public static Map<String, AES256> instances = new HashMap<>();

    public static AES256 getInstance(String deviceID) {
        return instances.computeIfAbsent(deviceID, k -> new AES256()); // todo AES256 constructor with key as parameter
    }

    private final BlockCipher AESCipher = new AESEngine();

    private PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(AESCipher, new PKCS7Padding());
    private KeyParameter key;

    public void setKey(byte[] key) {
        this.key = new KeyParameter(key);
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
