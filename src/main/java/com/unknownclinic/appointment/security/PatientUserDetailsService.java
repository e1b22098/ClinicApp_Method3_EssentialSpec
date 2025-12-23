package com.unknownclinic.appointment.security;

import com.unknownclinic.appointment.domain.Patient;
import com.unknownclinic.appointment.mapper.PatientMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PatientUserDetailsService implements UserDetailsService {
    
    private final PatientMapper patientMapper;

    public PatientUserDetailsService(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String patientNumber) throws UsernameNotFoundException {
        Patient patient = patientMapper.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new UsernameNotFoundException("診察券番号が見つかりません: " + patientNumber));
        
        return new PatientUserDetails(patient);
    }
}
