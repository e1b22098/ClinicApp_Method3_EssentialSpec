package com.unknownclinic.appointment.security;

import com.unknownclinic.appointment.domain.Administrator;
import com.unknownclinic.appointment.mapper.AdministratorMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {
    
    private final AdministratorMapper administratorMapper;

    public AdminUserDetailsService(AdministratorMapper administratorMapper) {
        this.administratorMapper = administratorMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String adminLoginId) throws UsernameNotFoundException {
        Administrator administrator = administratorMapper.findByAdminLoginId(adminLoginId)
            .orElseThrow(() -> new UsernameNotFoundException("管理者IDが見つかりません: " + adminLoginId));
        
        return new AdminUserDetails(administrator);
    }
}
