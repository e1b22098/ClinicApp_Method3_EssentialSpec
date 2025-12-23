package com.unknownclinic.appointment.config;

import com.unknownclinic.appointment.mapper.AdministratorMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 管理者パスワードの初期化（デバッグ用）
 * アプリケーション起動時に管理者パスワードを確認・更新する
 */
@Component
public class AdminPasswordInitializer implements CommandLineRunner {
    
    private final AdministratorMapper administratorMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminPasswordInitializer(AdministratorMapper administratorMapper, PasswordEncoder passwordEncoder) {
        this.administratorMapper = administratorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 管理者が存在するか確認
        administratorMapper.findByAdminLoginId("admin").ifPresentOrElse(
            admin -> {
                System.out.println("管理者が見つかりました: " + admin.getAdminLoginId());
                System.out.println("現在のパスワードハッシュ: " + admin.getPassword().substring(0, Math.min(30, admin.getPassword().length())));
                
                // パスワード検証テスト
                boolean matches = passwordEncoder.matches("admin", admin.getPassword());
                System.out.println("パスワード 'admin' の検証結果: " + matches);
                
                if (!matches) {
                    System.out.println("警告: パスワードが一致しません。新しいハッシュを生成します。");
                    String newHash = passwordEncoder.encode("admin");
                    System.out.println("新しいハッシュ: " + newHash);
                    System.out.println("以下のSQLを実行してください:");
                    System.out.println("UPDATE administrators SET password = '" + newHash + "' WHERE admin_login_id = 'admin';");
                }
            },
            () -> {
                System.out.println("警告: 管理者が見つかりません。");
                System.out.println("以下のSQLを実行して管理者を作成してください:");
                String hash = passwordEncoder.encode("admin");
                System.out.println("INSERT INTO administrators (admin_login_id, password, name) VALUES ('admin', '" + hash + "', 'システム管理者');");
            }
        );
    }
}
