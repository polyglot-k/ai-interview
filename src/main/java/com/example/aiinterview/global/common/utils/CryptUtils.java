package com.example.aiinterview.global.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class CryptUtils {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
