package com.unknownclinic.appointment.service;

import com.unknownclinic.appointment.domain.Patient;
import com.unknownclinic.appointment.mapper.PatientMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientMapper patientMapper, PasswordEncoder passwordEncoder) {
        this.patientMapper = patientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 診察券番号で患者を検索
     */
    @Transactional(readOnly = true)
    public Optional<Patient> findByPatientNumber(String patientNumber) {
        return patientMapper.findByPatientNumber(patientNumber);
    }

    /**
     * 新規患者登録
     */
    public Patient registerPatient(String patientNumber, String name, java.time.LocalDate birthDate, 
                                   String phoneNumber, String password) {
        // 既存の診察券番号チェック
        if (patientMapper.findByPatientNumber(patientNumber).isPresent()) {
            throw new IllegalArgumentException("この診察券番号は既に登録されています");
        }

        Patient patient = new Patient();
        patient.setPatientNumber(patientNumber);
        patient.setName(name);
        patient.setBirthDate(birthDate);
        patient.setPhoneNumber(phoneNumber);
        patient.setPassword(passwordEncoder.encode(password));
        
        patientMapper.insert(patient);
        return patient;
    }

    /**
     * パスワードリセットトークン生成
     */
    public void requestPasswordReset(String patientNumber) {
        Patient patient = patientMapper.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new IllegalArgumentException("診察券番号が見つかりません"));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24); // 24時間有効

        patientMapper.updateResetToken(patient.getPatientId(), resetToken, expiry);
    }

    /**
     * パスワードリセット実行
     */
    public void resetPassword(String resetToken, String newPassword) {
        Patient patient = patientMapper.findByResetToken(resetToken)
            .orElseThrow(() -> new IllegalArgumentException("無効または期限切れのリセットトークンです"));

        patientMapper.updatePassword(patient.getPatientId(), passwordEncoder.encode(newPassword));
        patientMapper.clearResetToken(patient.getPatientId());
    }

    /**
     * パスワード変更
     */
    public void changePassword(Long patientId, String currentPassword, String newPassword) {
        Patient patient = patientMapper.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("患者が見つかりません"));

        if (!passwordEncoder.matches(currentPassword, patient.getPassword())) {
            throw new IllegalArgumentException("現在のパスワードが正しくありません");
        }

        patientMapper.updatePassword(patientId, passwordEncoder.encode(newPassword));
    }

    /**
     * IDで患者を検索（内部用）
     */
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long patientId) {
        return patientMapper.findById(patientId);
    }
}
