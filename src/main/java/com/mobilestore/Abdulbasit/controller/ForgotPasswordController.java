package com.mobilestore.Abdulbasit.controller;

import com.mobilestore.Abdulbasit.entity.User;
import com.mobilestore.Abdulbasit.service.UserServices;
import com.mobilestore.Abdulbasit.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private MailService mailService;

    // 1. Forgot Password Page dikhana
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot_password";
    }

    // 2. Email par OTP bhejna (Using Resend API via MailService)
    @PostMapping("/forgot-password")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
        User user = userServices.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "This email is not registered!");
            return "forgot_password";
        }

        // 6-digit OTP generate karna
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Session mein save karna taaki verify kar sakein
        session.setAttribute("otp", otp);
        session.setAttribute("resetEmail", email);

        try {
            // ✅ Resend API logic call ho raha hai (Option A enabled in MailService)
            mailService.sendOTPEmail(email, otp);
            return "verify_otp";
        } catch (Exception e) {
            model.addAttribute("error", "Error sending email. Please try again.");
            return "forgot_password";
        }
    }

    // 3. OTP Verify karna
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") String userOtp, HttpSession session, Model model) {
        String sessionOtp = (String) session.getAttribute("otp");
        if (sessionOtp != null && sessionOtp.equals(userOtp)) {
            return "reset_password";
        }
        model.addAttribute("error", "Invalid OTP!");
        return "verify_otp";
    }

    // 4. Naya Password Update karna
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("password") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("resetEmail");
        if (email != null) {
            userServices.updatePassword(email, newPassword);
            session.invalidate(); // Password reset ke baad session clear
            return "redirect:/login?resetSuccess=true";
        }
        return "redirect:/forgot-password";
    }
}