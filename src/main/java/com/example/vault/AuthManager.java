package com.example.vault;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class AuthManager {
    private static final int AUTH_SALT_BYTES = 16;
    private static final int AES_SALT_BYTES = 16;
    private static final int AUTH_ITERATIONS = 200_000;
    private static final int AES_ITERATIONS = 200_000;
    private static final int KEY_BITS = 256;

    public static void register(char[] masterPassword, DBManager db) throws Exception {
        byte[] authSalt = CryptoUtils.generateRandomBytes(AUTH_SALT_BYTES);
        SecretKeySpec authKey = CryptoUtils.deriveKeyPBKDF2(masterPassword, authSalt, AUTH_ITERATIONS, KEY_BITS);
        byte[] authHash = authKey.getEncoded();

        byte[] aesSalt = CryptoUtils.generateRandomBytes(AES_SALT_BYTES);

        db.saveUserMeta(authSalt, authHash, AUTH_ITERATIONS, aesSalt, AES_ITERATIONS);
        // zero out sensitive copies
        Arrays.fill(authHash, (byte)0);
    }

    public static SecretKeySpec login(char[] masterPassword, DBManager db) throws Exception {
        DBManager.UserMeta meta = db.loadUserMeta();
        if (meta == null) return null;
        SecretKeySpec authDerived = CryptoUtils.deriveKeyPBKDF2(masterPassword, meta.authSalt, meta.authIterations, KEY_BITS);
        byte[] candidate = authDerived.getEncoded();
        boolean ok = constantTimeEquals(candidate, meta.authHash);
        Arrays.fill(candidate, (byte)0);
        if (!ok) return null;
        // derive AES key
        SecretKeySpec aesKey = CryptoUtils.deriveKeyPBKDF2(masterPassword, meta.aesSalt, meta.aesIterations, KEY_BITS);
        return aesKey;
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) result |= a[i] ^ b[i];
        return result == 0;
    }
}
