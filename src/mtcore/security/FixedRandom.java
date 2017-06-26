package mtcore.security;

import mtcore.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FixedRandom extends SecureRandom {
    public static final FixedRandom instance = create();

    public static FixedRandom create() { return new FixedRandom(); }

    private static final String TAG = "FixedRandom";

    MessageDigest sha;
    byte[] state;

    FixedRandom() {
        try {
            this.sha = MessageDigest.getInstance("SHA-256");
            this.state = sha.digest();
        } catch (NoSuchAlgorithmException ex) {
            Log.e(TAG, "SHA-256 algorithm was not found in this device.");
        }
    }

    public void nextBytes(byte[] bytes) {
        int offset;

        for (offset = 0; offset < bytes.length && state.length <= bytes.length - offset; offset += state.length) {
            sha.update(state);

            state = sha.digest();

            System.arraycopy(state, 0, bytes, offset, state.length);
        }

        System.arraycopy(state, 0, bytes, offset, bytes.length - offset);

        sha.update(state);
    }
}