package com.unknownclinic.appointment.mapper;

import com.unknownclinic.appointment.domain.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BookingMapper {
    List<Booking> findByPatientId(@Param("patientId") Long patientId);
    List<Booking> findByBusinessDate(@Param("businessDate") LocalDate businessDate);
    Optional<Booking> findById(@Param("bookingId") Long bookingId);
    Optional<Booking> findByBusinessDateAndTimeSlot(@Param("businessDate") LocalDate businessDate, @Param("timeSlot") String timeSlot);
    void insert(Booking booking);
    void update(Booking booking);
    void updateStatus(@Param("bookingId") Long bookingId, @Param("status") String status);
    void delete(@Param("bookingId") Long bookingId);
}
