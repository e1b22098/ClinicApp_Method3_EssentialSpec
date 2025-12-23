package com.unknownclinic.appointment.controller;

import com.unknownclinic.appointment.dto.PatientRegistrationDto;
import com.unknownclinic.appointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(PatientService patientService, PasswordEncoder passwordEncoder) {
        this.patientService = patientService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * トップページ
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * ログインページ（Spring Securityが処理）
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 新規患者登録フォーム表示
     */
    @GetMapping("/patient/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new PatientRegistrationDto());
        return "patient/register";
    }

    /**
     * 新規患者登録処理
     */
    @PostMapping("/patient/register")
    public String register(@Valid @ModelAttribute("registrationDto") PatientRegistrationDto dto,
                          BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "patient/register";
        }

        // パスワード確認
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "パスワードが一致しません");
            return "patient/register";
        }

        try {
            patientService.registerPatient(
                dto.getPatientNumber(),
                dto.getName(),
                dto.getBirthDate(),
                dto.getPhoneNumber(),
                dto.getPassword()
            );
            redirectAttributes.addFlashAttribute("message", "登録が完了しました。ログインしてください。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("patientNumber", "error.patientNumber", e.getMessage());
            return "patient/register";
        }
    }

    /**
     * パスワードリセット申請フォーム
     */
    @GetMapping("/patient/reset-password")
    public String showResetPasswordRequestForm() {
        return "patient/reset-password-request";
    }

    /**
     * パスワードリセット申請処理
     */
    @PostMapping("/patient/reset-password/request")
    public String requestPasswordReset(@RequestParam String patientNumber,
                                      RedirectAttributes redirectAttributes) {
        try {
            patientService.requestPasswordReset(patientNumber);
            redirectAttributes.addFlashAttribute("message", "パスワードリセット用のリンクをメールで送信しました（実装簡略化のため、実際のメール送信は省略）");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/reset-password";
    }

    /**
     * パスワードリセット実行フォーム
     */
    @GetMapping("/patient/reset-password/{token}")
    public String showResetPasswordForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "patient/reset-password";
    }

    /**
     * パスワードリセット実行
     */
    @PostMapping("/patient/reset-password/{token}")
    public String resetPassword(@PathVariable String token,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "パスワードが一致しません");
            return "redirect:/patient/reset-password/" + token;
        }

        try {
            patientService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("message", "パスワードがリセットされました。ログインしてください。");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/reset-password/" + token;
        }
    }
}
