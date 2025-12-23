package com.unknownclinic.appointment.config;

import com.unknownclinic.appointment.security.AdminUserDetailsService;
import com.unknownclinic.appointment.security.PatientUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.unknownclinic.appointment.config.CustomAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final PatientUserDetailsService patientUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(PatientUserDetailsService patientUserDetailsService,
                         AdminUserDetailsService adminUserDetailsService,
                         CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.patientUserDetailsService = patientUserDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(patientUserDetailsService, adminUserDetailsService, passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公開エンドポイント
                .requestMatchers("/", "/patient/register", "/patient/reset-password/**", 
                               "/css/**", "/js/**", "/images/**").permitAll()
                // 患者用エンドポイント
                .requestMatchers("/patient/**").hasRole("PATIENT")
                // 管理者用エンドポイント
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
