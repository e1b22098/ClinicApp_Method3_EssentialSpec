package com.unknownclinic.appointment.service;

import com.unknownclinic.appointment.domain.Booking;
import com.unknownclinic.appointment.domain.BusinessDay;
import com.unknownclinic.appointment.mapper.BookingMapper;
import com.unknownclinic.appointment.mapper.BusinessDayMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookingService {
    
    private final BookingMapper bookingMapper;
    private final BusinessDayMapper businessDayMapper;
    
    // 利用可能な時間枠
    private static final String[] TIME_SLOTS = {
        "09:00-09:30", "09:30-10:00", "10:00-10:30", "10:30-11:00",
        "11:00-11:30", "11:30-12:00", "13:00-13:30", "13:30-14:00",
        "14:00-14:30", "14:30-15:00", "15:00-15:30", "15:30-16:00"
    };

    public BookingService(BookingMapper bookingMapper, BusinessDayMapper businessDayMapper) {
        this.bookingMapper = bookingMapper;
        this.businessDayMapper = businessDayMapper;
    }

    /**
     * 患者IDに基づいて予約一覧を取得
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByPatientId(Long patientId) {
        return bookingMapper.findByPatientId(patientId);
    }

    /**
     * 営業日に基づいて予約一覧を取得
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByBusinessDate(LocalDate businessDate) {
        return bookingMapper.findByBusinessDate(businessDate);
    }

    /**
     * 予約を作成（確認待ち状態で）
     */
    public Booking createBooking(Long patientId, LocalDate businessDate, String timeSlot) {
        // 営業日が存在し、予約受付可能かチェック
        BusinessDay businessDay = businessDayMapper.findByBusinessDate(businessDate)
            .orElseThrow(() -> new IllegalArgumentException("指定された日は営業日ではありません"));
        
        if (!businessDay.getIsAvailable()) {
            throw new IllegalStateException("この日は予約受付が停止されています");
        }

        // 既存の予約があるかチェック（重複チェック）
        bookingMapper.findByBusinessDateAndTimeSlot(businessDate, timeSlot)
            .ifPresent(existing -> {
                throw new IllegalStateException("この時間枠は既に予約されています");
            });

        Booking booking = new Booking();
        booking.setPatientId(patientId);
        booking.setBusinessDate(businessDate);
        booking.setTimeSlot(timeSlot);
        booking.setStatus("PENDING");
        
        bookingMapper.insert(booking);
        return booking;
    }

    /**
     * 予約を確定（PENDINGからCONFIRMEDへ）
     */
    public void confirmBooking(Long bookingId, Long patientId) {
        Booking booking = bookingMapper.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));

        if (!booking.getPatientId().equals(patientId)) {
            throw new SecurityException("この予約にアクセスする権限がありません");
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException("確定できない予約です");
        }

        bookingMapper.updateStatus(bookingId, "CONFIRMED");
    }

    /**
     * 予約をキャンセル
     */
    public void cancelBooking(Long bookingId, Long patientId) {
        Booking booking = bookingMapper.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));

        if (!booking.getPatientId().equals(patientId)) {
            throw new SecurityException("この予約にアクセスする権限がありません");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalStateException("既にキャンセル済みの予約です");
        }

        bookingMapper.updateStatus(bookingId, "CANCELLED");
    }

    /**
     * 利用可能な時間枠を取得
     */
    public static String[] getAvailableTimeSlots() {
        return TIME_SLOTS.clone();
    }
}
