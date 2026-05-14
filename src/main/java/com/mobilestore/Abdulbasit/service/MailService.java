package com.mobilestore.Abdulbasit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private TemplateEngine templateEngine;

    // Railway variables se uthayega
    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public void sendOrderEmail(String toEmail, String userName, String productName, String productImage, String totalAmount, String quantity, String address) {
        try {
            // 1. Thymeleaf Context (Wahi purana logic)
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("productName", productName);
            context.setVariable("productImage", productImage);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("quantity", quantity);
            context.setVariable("address", address);

            String htmlContent = templateEngine.process("order-confirmation", context);

            // 2. API Request Setup
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. Payload (Resend format)
            Map<String, Object> body = new HashMap<>();
            body.put("from", "onboarding@resend.dev"); // Testing ke liye ye default address hai
            body.put("to", toEmail);
            body.put("subject", "Order Confirmed! - Mobile Store");
            body.put("html", htmlContent);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 4. Hit the API
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.resend.com/emails", entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("✅ Email sent successfully via API!");
            }

        } catch (Exception e) {
            System.err.println("❌ API Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MailService.java mein ye naya method add karo (sendOrderEmail ke niche)

    public void sendOTPEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("from", "onboarding@resend.dev");


            body.put("to", "projectabdulbasit09@gmail.com");

            body.put("subject", "Password Reset OTP - Mobile Store");
            body.put("html", "<p>Your OTP for password reset is: <strong>" + otp + "</strong></p><p>Valid for 1 minute.</p>");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity("https://api.resend.com/emails", entity, String.class);

            System.out.println("✅ OTP Sent successfully to verified email!");
        } catch (Exception e) {
            System.err.println("❌ OTP API Error: " + e.getMessage());
        }
    }
}