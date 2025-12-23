package com.unknownclinic.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingDto {
    
    @NotNull(message = "予約日は必須です")
    private LocalDate businessDate;
    
    @NotBlank(message = "時間枠は必須です")
    private String timeSlot;

    // Getters and Setters
    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
