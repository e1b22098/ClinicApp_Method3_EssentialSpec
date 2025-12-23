package com.unknownclinic.appointment.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 営業日エンティティ
 */
public class BusinessDay {
    private Long businessDayId;
    private LocalDate businessDate;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getBusinessDayId() {
        return businessDayId;
    }

    public void setBusinessDayId(Long businessDayId) {
        this.businessDayId = businessDayId;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
