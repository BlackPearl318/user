package com.user.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;

@Component
public class PasswordUtils {

    // 生成密码哈希
    public static String hash(String password) {
        // 生成盐值
        String salt = BCrypt.gensalt(12, new SecureRandom());
        return BCrypt.hashpw(password, salt);
    }

    // 验证密码
    public static boolean check(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}

