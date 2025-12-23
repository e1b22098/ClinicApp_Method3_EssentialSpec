package com.unknownclinic.appointment.service;

import com.unknownclinic.appointment.domain.Booking;
import com.unknownclinic.appointment.domain.BusinessDay;
import com.unknownclinic.appointment.mapper.BookingMapper;
import com.unknownclinic.appointment.mapper.BusinessDayMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService ユニットテスト")
class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BusinessDayMapper businessDayMapper;

    @InjectMocks
    private BookingService bookingService;

    private Long patientId;
    private LocalDate businessDate;
    private String timeSlot;
    private BusinessDay businessDay;
    private Booking booking;

    @BeforeEach
    void setUp() {
        patientId = 1L;
        businessDate = LocalDate.now().plusDays(7);
        timeSlot = "09:00-09:30";
        
        businessDay = new BusinessDay();
        businessDay.setBusinessDayId(1L);
        businessDay.setBusinessDate(businessDate);
        businessDay.setIsAvailable(true);
        
        booking = new Booking();
        booking.setBookingId(1L);
        booking.setPatientId(patientId);
        booking.setBusinessDate(businessDate);
        booking.setTimeSlot(timeSlot);
        booking.setStatus("PENDING");
    }

    @Test
    @DisplayName("正常系: 患者IDで予約一覧を取得できる")
    void testGetBookingsByPatientId_Success() {
        // Given
        List<Booking> expectedBookings = Arrays.asList(booking);
        when(bookingMapper.findByPatientId(patientId)).thenReturn(expectedBookings);

        // When
        List<Booking> result = bookingService.getBookingsByPatientId(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getBookingId(), result.get(0).getBookingId());
        verify(bookingMapper, times(1)).findByPatientId(patientId);
    }

    @Test
    @DisplayName("正常系: 営業日で予約一覧を取得できる")
    void testGetBookingsByBusinessDate_Success() {
        // Given
        List<Booking> expectedBookings = Arrays.asList(booking);
        when(bookingMapper.findByBusinessDate(businessDate)).thenReturn(expectedBookings);

        // When
        List<Booking> result = bookingService.getBookingsByBusinessDate(businessDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingMapper, times(1)).findByBusinessDate(businessDate);
    }

    @Test
    @DisplayName("正常系: 予約を作成できる")
    void testCreateBooking_Success() {
        // Given
        when(businessDayMapper.findByBusinessDate(businessDate))
            .thenReturn(Optional.of(businessDay));
        when(bookingMapper.findByBusinessDateAndTimeSlot(businessDate, timeSlot))
            .thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setBookingId(1L);
            return null;
        }).when(bookingMapper).insert(any(Booking.class));

        // When
        Booking result = bookingService.createBooking(patientId, businessDate, timeSlot);

        // Then
        assertNotNull(result);
        assertEquals(patientId, result.getPatientId());
        assertEquals(businessDate, result.getBusinessDate());
        assertEquals(timeSlot, result.getTimeSlot());
        assertEquals("PENDING", result.getStatus());
        verify(businessDayMapper, times(1)).findByBusinessDate(businessDate);
        verify(bookingMapper, times(1)).findByBusinessDateAndTimeSlot(businessDate, timeSlot);
        verify(bookingMapper, times(1)).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 営業日が存在しない場合、予約作成に失敗する")
    void testCreateBooking_Fail_BusinessDayNotFound() {
        // Given
        when(businessDayMapper.findByBusinessDate(businessDate))
            .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.createBooking(patientId, businessDate, timeSlot)
        );
        assertEquals("指定された日は営業日ではありません", exception.getMessage());
        verify(businessDayMapper, times(1)).findByBusinessDate(businessDate);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 予約受付が停止されている場合、予約作成に失敗する")
    void testCreateBooking_Fail_BusinessDayNotAvailable() {
        // Given
        businessDay.setIsAvailable(false);
        when(businessDayMapper.findByBusinessDate(businessDate))
            .thenReturn(Optional.of(businessDay));

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> bookingService.createBooking(patientId, businessDate, timeSlot)
        );
        assertEquals("この日は予約受付が停止されています", exception.getMessage());
        verify(businessDayMapper, times(1)).findByBusinessDate(businessDate);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 既に予約されている時間枠の場合、予約作成に失敗する")
    void testCreateBooking_Fail_TimeSlotAlreadyBooked() {
        // Given
        when(businessDayMapper.findByBusinessDate(businessDate))
            .thenReturn(Optional.of(businessDay));
        when(bookingMapper.findByBusinessDateAndTimeSlot(businessDate, timeSlot))
            .thenReturn(Optional.of(booking));

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> bookingService.createBooking(patientId, businessDate, timeSlot)
        );
        assertEquals("この時間枠は既に予約されています", exception.getMessage());
        verify(businessDayMapper, times(1)).findByBusinessDate(businessDate);
        verify(bookingMapper, times(1)).findByBusinessDateAndTimeSlot(businessDate, timeSlot);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("正常系: 予約を確定できる")
    void testConfirmBooking_Success() {
        // Given
        Long bookingId = 1L;
        booking.setStatus("PENDING");
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When
        bookingService.confirmBooking(bookingId, patientId);

        // Then
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).updateStatus(bookingId, "CONFIRMED");
    }

    @Test
    @DisplayName("異常系: 予約が見つからない場合、確定に失敗する")
    void testConfirmBooking_Fail_BookingNotFound() {
        // Given
        Long bookingId = 999L;
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.confirmBooking(bookingId, patientId)
        );
        assertEquals("予約が見つかりません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("異常系: 他の患者の予約を確定しようとした場合、失敗する")
    void testConfirmBooking_Fail_Unauthorized() {
        // Given
        Long bookingId = 1L;
        Long otherPatientId = 2L;
        booking.setPatientId(otherPatientId);
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> bookingService.confirmBooking(bookingId, patientId)
        );
        assertEquals("この予約にアクセスする権限がありません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("異常系: 既に確定済みの予約を確定しようとした場合、失敗する")
    void testConfirmBooking_Fail_AlreadyConfirmed() {
        // Given
        Long bookingId = 1L;
        booking.setStatus("CONFIRMED");
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> bookingService.confirmBooking(bookingId, patientId)
        );
        assertEquals("確定できない予約です", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("正常系: 予約をキャンセルできる")
    void testCancelBooking_Success() {
        // Given
        Long bookingId = 1L;
        booking.setStatus("CONFIRMED");
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When
        bookingService.cancelBooking(bookingId, patientId);

        // Then
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).updateStatus(bookingId, "CANCELLED");
    }

    @Test
    @DisplayName("異常系: 予約が見つからない場合、キャンセルに失敗する")
    void testCancelBooking_Fail_BookingNotFound() {
        // Given
        Long bookingId = 999L;
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.cancelBooking(bookingId, patientId)
        );
        assertEquals("予約が見つかりません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("異常系: 他の患者の予約をキャンセルしようとした場合、失敗する")
    void testCancelBooking_Fail_Unauthorized() {
        // Given
        Long bookingId = 1L;
        Long otherPatientId = 2L;
        booking.setPatientId(otherPatientId);
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> bookingService.cancelBooking(bookingId, patientId)
        );
        assertEquals("この予約にアクセスする権限がありません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("異常系: 既にキャンセル済みの予約をキャンセルしようとした場合、失敗する")
    void testCancelBooking_Fail_AlreadyCancelled() {
        // Given
        Long bookingId = 1L;
        booking.setStatus("CANCELLED");
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> bookingService.cancelBooking(bookingId, patientId)
        );
        assertEquals("既にキャンセル済みの予約です", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyLong(), anyString());
    }

    @Test
    @DisplayName("正常系: 利用可能な時間枠を取得できる")
    void testGetAvailableTimeSlots_Success() {
        // When
        String[] timeSlots = BookingService.getAvailableTimeSlots();

        // Then
        assertNotNull(timeSlots);
        assertTrue(timeSlots.length > 0);
        assertEquals("09:00-09:30", timeSlots[0]);
    }
}
