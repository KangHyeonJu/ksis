package com.boot.ksis.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";
//    private static final int KEY_SIZE = 128; // or 256
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // AES Key 생성
//    public static SecretKey generateKey() throws Exception {
//        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
//        keyGen.init(KEY_SIZE);
//        return keyGen.generateKey();
//    }
    // AES Key로부터 SecretKeySpec 생성
//    public static SecretKeySpec getKeySpec(byte[] key) {
//        return new SecretKeySpec(key, ALGORITHM);
//    }

    // 암호화
    public static String encrypt(String data, SecretKeySpec keySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 복호화
    public static String decrypt(String encryptedData, SecretKeySpec keySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted);
    }
}
