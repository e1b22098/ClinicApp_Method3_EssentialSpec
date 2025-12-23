package com.unknownclinic.appointment.controller;

import com.unknownclinic.appointment.domain.Booking;
import com.unknownclinic.appointment.domain.BusinessDay;
import com.unknownclinic.appointment.security.AdminUserDetails;
import com.unknownclinic.appointment.service.BookingService;
import com.unknownclinic.appointment.service.BusinessDayService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final BookingService bookingService;
    private final BusinessDayService businessDayService;

    public AdminController(BookingService bookingService, BusinessDayService businessDayService) {
        this.bookingService = bookingService;
        this.businessDayService = businessDayService;
    }

    /**
     * 管理者ダッシュボード
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            List<BusinessDay> businessDays = businessDayService.getAllBusinessDays();
            model.addAttribute("businessDays", businessDays);
            return "admin/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "営業日データの取得に失敗しました: " + e.getMessage());
            model.addAttribute("businessDays", java.util.Collections.emptyList());
            return "admin/dashboard";
        }
    }

    /**
     * 予約状況一覧（日別）
     */
    @GetMapping("/bookings")
    public String bookings(@RequestParam(required = false) LocalDate date, Model model) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<Booking> bookings = bookingService.getBookingsByBusinessDate(targetDate);
        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedDate", targetDate);
        return "admin/bookings";
    }

    /**
     * 営業日設定一覧
     */
    @GetMapping("/business-days")
    public String businessDays(Model model) {
        try {
            List<BusinessDay> businessDays = businessDayService.getAllBusinessDays();
            model.addAttribute("businessDays", businessDays);
            return "admin/business-days";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "営業日データの取得に失敗しました: " + e.getMessage());
            model.addAttribute("businessDays", java.util.Collections.emptyList());
            return "admin/business-days";
        }
    }

    /**
     * 営業日追加
     */
    @PostMapping("/business-days")
    public String addBusinessDay(@RequestParam LocalDate businessDate,
                                RedirectAttributes redirectAttributes) {
        try {
            businessDayService.addBusinessDay(businessDate);
            redirectAttributes.addFlashAttribute("message", "営業日を追加しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/business-days";
    }

    /**
     * 営業日削除
     */
    @PostMapping("/business-days/{businessDayId}/delete")
    public String deleteBusinessDay(@PathVariable Long businessDayId,
                                   RedirectAttributes redirectAttributes) {
        try {
            businessDayService.deleteBusinessDay(businessDayId);
            redirectAttributes.addFlashAttribute("message", "営業日を削除しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/business-days";
    }

    /**
     * 予約受付停止・再開
     */
    @PostMapping("/business-days/{businessDate}/availability")
    public String updateAvailability(@PathVariable LocalDate businessDate,
                                    @RequestParam Boolean isAvailable,
                                    RedirectAttributes redirectAttributes) {
        try {
            businessDayService.updateAvailability(businessDate, isAvailable);
            redirectAttributes.addFlashAttribute("message", 
                isAvailable ? "予約受付を再開しました" : "予約受付を停止しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/business-days";
    }
}
