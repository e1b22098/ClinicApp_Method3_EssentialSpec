package com.unknownclinic.appointment.mapper;

import com.unknownclinic.appointment.domain.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PatientMapper {
    Optional<Patient> findById(@Param("patientId") Long patientId);
    Optional<Patient> findByPatientNumber(@Param("patientNumber") String patientNumber);
    Optional<Patient> findByResetToken(@Param("resetToken") String resetToken);
    void insert(Patient patient);
    void update(Patient patient);
    void updatePassword(@Param("patientId") Long patientId, @Param("password") String password);
    void updateResetToken(@Param("patientId") Long patientId, @Param("resetToken") String resetToken, @Param("expiry") java.time.LocalDateTime expiry);
    void clearResetToken(@Param("patientId") Long patientId);
}
