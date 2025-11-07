package com.example.vault;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] generateRandomBytes(int len) {
        byte[] b = new byte[len];
        RANDOM.nextBytes(b);
        return b;
    }

    public static SecretKeySpec deriveKeyPBKDF2(char[] password, byte[] salt, int iterations, int keyBits) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyBits);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public static class EncryptedData {
        public final String cipherTextBase64;
        public final String ivBase64;

        public EncryptedData(String cipherTextBase64, String ivBase64) {
            this.cipherTextBase64 = cipherTextBase64;
            this.ivBase64 = ivBase64;
        }
    }

    public static EncryptedData encryptAESGCM(byte[] plaintext, SecretKeySpec key) throws Exception {
        byte[] iv = generateRandomBytes(12); // 96-bit IV recommended for GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ct = cipher.doFinal(plaintext);
        String ctB = Base64.getEncoder().encodeToString(ct);
        String ivB = Base64.getEncoder().encodeToString(iv);
        return new EncryptedData(ctB, ivB);
    }

    public static byte[] decryptAESGCM(String cipherTextBase64, String ivBase64, SecretKeySpec key) throws Exception {
        byte[] ct = Base64.getDecoder().decode(cipherTextBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(ct);
    }
}
