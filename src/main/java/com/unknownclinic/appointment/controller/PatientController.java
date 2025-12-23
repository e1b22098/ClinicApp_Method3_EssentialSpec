package com.unknownclinic.appointment.controller;

import com.unknownclinic.appointment.domain.Booking;
import com.unknownclinic.appointment.domain.BusinessDay;
import com.unknownclinic.appointment.dto.BookingDto;
import com.unknownclinic.appointment.dto.PatientRegistrationDto;
import com.unknownclinic.appointment.security.PatientUserDetails;
import com.unknownclinic.appointment.service.BookingService;
import com.unknownclinic.appointment.service.BusinessDayService;
import com.unknownclinic.appointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    
    private final PatientService patientService;
    private final BookingService bookingService;
    private final BusinessDayService businessDayService;

    public PatientController(PatientService patientService, BookingService bookingService,
                            BusinessDayService businessDayService) {
        this.patientService = patientService;
        this.bookingService = bookingService;
        this.businessDayService = businessDayService;
    }

    /**
     * 患者ダッシュボード（予約一覧）
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal PatientUserDetails userDetails, Model model) {
        Long patientId = userDetails.getPatient().getPatientId();
        List<Booking> bookings = bookingService.getBookingsByPatientId(patientId);
        model.addAttribute("bookings", bookings);
        return "patient/dashboard";
    }

    /**
     * 予約作成フォーム表示
     */
    @GetMapping("/bookings/new")
    public String showBookingForm(Model model) {
        List<BusinessDay> availableDays = businessDayService.getAvailableBusinessDays();
        model.addAttribute("availableDays", availableDays);
        model.addAttribute("timeSlots", BookingService.getAvailableTimeSlots());
        model.addAttribute("bookingDto", new BookingDto());
        return "patient/booking-form";
    }

    /**
     * 予約作成
     */
    @PostMapping("/bookings")
    public String createBooking(@AuthenticationPrincipal PatientUserDetails userDetails,
                               @Valid @ModelAttribute BookingDto bookingDto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<BusinessDay> availableDays = businessDayService.getAvailableBusinessDays();
            model.addAttribute("availableDays", availableDays);
            model.addAttribute("timeSlots", BookingService.getAvailableTimeSlots());
            return "patient/booking-form";
        }

        try {
            Long patientId = userDetails.getPatient().getPatientId();
            Booking booking = bookingService.createBooking(
                patientId, bookingDto.getBusinessDate(), bookingDto.getTimeSlot());
            redirectAttributes.addFlashAttribute("bookingId", booking.getBookingId());
            return "redirect:/patient/bookings/confirm";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<BusinessDay> availableDays = businessDayService.getAvailableBusinessDays();
            model.addAttribute("availableDays", availableDays);
            model.addAttribute("timeSlots", BookingService.getAvailableTimeSlots());
            return "patient/booking-form";
        }
    }

    /**
     * 予約確認画面
     */
    @GetMapping("/bookings/confirm")
    public String confirmBooking(@RequestParam(required = false) Long bookingId,
                                @AuthenticationPrincipal PatientUserDetails userDetails,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bookingId == null) {
            bookingId = (Long) model.asMap().get("bookingId");
        }
        
        if (bookingId == null) {
            redirectAttributes.addFlashAttribute("error", "予約IDが指定されていません");
            return "redirect:/patient/dashboard";
        }

        final Long finalBookingId = bookingId;
        try {
            List<Booking> bookings = bookingService.getBookingsByPatientId(userDetails.getPatient().getPatientId());
            Booking booking = bookings.stream()
                .filter(b -> b.getBookingId().equals(finalBookingId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("予約が見つかりません"));
            
            model.addAttribute("booking", booking);
            return "patient/booking-confirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/dashboard";
        }
    }

    /**
     * 予約確定
     */
    @PostMapping("/bookings/{bookingId}/confirm")
    public String confirmBookingAction(@PathVariable Long bookingId,
                                      @AuthenticationPrincipal PatientUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            bookingService.confirmBooking(bookingId, userDetails.getPatient().getPatientId());
            redirectAttributes.addFlashAttribute("message", "予約が確定されました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/dashboard";
    }

    /**
     * 予約キャンセル
     */
    @PostMapping("/bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                               @AuthenticationPrincipal PatientUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId, userDetails.getPatient().getPatientId());
            redirectAttributes.addFlashAttribute("message", "予約がキャンセルされました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/dashboard";
    }
}
