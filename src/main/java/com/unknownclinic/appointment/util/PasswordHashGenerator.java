package com.unknownclinic.appointment.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワードハッシュ生成ユーティリティ
 * 管理者パスワードのハッシュを生成するために使用
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hashed: " + hashedPassword);
        System.out.println("\n以下のSQLを実行してください：");
        System.out.println("UPDATE administrators SET password = '" + hashedPassword + "' WHERE admin_login_id = 'admin';");
    }
}
