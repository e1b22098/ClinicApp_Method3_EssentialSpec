package com.unknownclinic.appointment.security;

import com.unknownclinic.appointment.domain.Administrator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 管理者認証用のUserDetails実装
 */
public class AdminUserDetails implements UserDetails {
    
    private final Administrator administrator;

    public AdminUserDetails(Administrator administrator) {
        this.administrator = administrator;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return administrator.getPassword();
    }

    @Override
    public String getUsername() {
        return administrator.getAdminLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
