package com.unknownclinic.appointment.security;

import com.unknownclinic.appointment.domain.Patient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 患者認証用のUserDetails実装
 */
public class PatientUserDetails implements UserDetails {
    
    private final Patient patient;

    public PatientUserDetails(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"));
    }

    @Override
    public String getPassword() {
        return patient.getPassword();
    }

    @Override
    public String getUsername() {
        return patient.getPatientNumber();
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
