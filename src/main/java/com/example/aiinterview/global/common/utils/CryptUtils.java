package com.example.aiinterview.global.common.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hash(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        // Salt와 해시된 비밀번호를 함께 저장하기 위해 문자열 형태로 결합 (예: "salt$hashedPassword")
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hashedPassword);
    }

    public static boolean matches(String rawPassword, String hashedPasswordWithSalt) {
        String[] parts = hashedPasswordWithSalt.split("\\$");
        if (parts.length != 2) {
            // 저장된 형태가 "salt$hashedPassword"가 아니면 비교 불가
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        byte[] actualHash = hash(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return java.util.Arrays.equals(actualHash, expectedHash);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hash(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}