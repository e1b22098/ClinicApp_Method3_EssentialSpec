package com.unknownclinic.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class PatientRegistrationDto {
    
    @NotBlank(message = "診察券番号は必須です")
    private String patientNumber;
    
    @NotBlank(message = "氏名は必須です")
    private String name;
    
    @NotNull(message = "生年月日は必須です")
    private LocalDate birthDate;
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "\\d{10,11}", message = "電話番号の形式が正しくありません")
    private String phoneNumber;
    
    @NotBlank(message = "パスワードは必須です")
    private String password;
    
    @NotBlank(message = "パスワード（確認）は必須です")
    private String confirmPassword;

    // Getters and Setters
    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
