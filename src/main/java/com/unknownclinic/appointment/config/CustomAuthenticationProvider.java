package com.unknownclinic.appointment.config;

import com.unknownclinic.appointment.security.AdminUserDetails;
import com.unknownclinic.appointment.security.AdminUserDetailsService;
import com.unknownclinic.appointment.security.PatientUserDetails;
import com.unknownclinic.appointment.security.PatientUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 患者と管理者の両方をサポートするカスタム認証プロバイダー
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    private final PatientUserDetailsService patientUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(PatientUserDetailsService patientUserDetailsService,
                                       AdminUserDetailsService adminUserDetailsService,
                                       PasswordEncoder passwordEncoder) {
        this.patientUserDetailsService = patientUserDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // まず管理者として認証を試みる
        try {
            AdminUserDetails adminUserDetails = (AdminUserDetails) adminUserDetailsService.loadUserByUsername(username);
            boolean passwordMatches = passwordEncoder.matches(password, adminUserDetails.getPassword());
            if (passwordMatches) {
                return new UsernamePasswordAuthenticationToken(adminUserDetails, password, adminUserDetails.getAuthorities());
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            // 管理者が見つからない場合は患者として試みる
        } catch (Exception e) {
            // その他のエラーは無視して患者として試みる
        }

        // 患者として認証を試みる
        try {
            PatientUserDetails patientUserDetails = (PatientUserDetails) patientUserDetailsService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, patientUserDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(patientUserDetails, password, patientUserDetails.getAuthorities());
            }
        } catch (Exception e) {
            // 患者としても認証できなかった
        }

        throw new BadCredentialsException("ログインIDまたはパスワードが正しくありません");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
