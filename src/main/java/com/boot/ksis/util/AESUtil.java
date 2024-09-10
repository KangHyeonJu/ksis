package com.boot.ksis.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";
//    private static final int KEY_SIZE = 128; // or 256
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";


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

    // AES 암호화 - IV 포함
    public static String encrypt(String data, SecretKeySpec keySpec) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // 무작위 IV 생성
        byte[] iv = new byte[16];  // AES 블록 크기 (16바이트)
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 암호화 초기화
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        // IV와 암호문 결합
        byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

        // Base64로 인코딩
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    // AES 복호화 - IV 포함
    public static String decrypt(String encryptedData, SecretKeySpec keySpec) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);

        // 저장된 데이터에서 IV 추출
        byte[] iv = new byte[16];  // AES 블록 크기 (16바이트)
        System.arraycopy(decodedBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 암호문 추출
        byte[] encryptedBytes = new byte[decodedBytes.length - iv.length];
        System.arraycopy(decodedBytes, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // 복호화 초기화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted);
    }
}
