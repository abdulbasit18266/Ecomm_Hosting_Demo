package com.mobilestore.Abdulbasit.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendOrderEmail(String toEmail, String userName, String productName, String productImage, String totalAmount, String quantity, String address) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("productName", productName);
            context.setVariable("productImage", productImage);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("quantity", quantity);
            context.setVariable("address", address);

            // ✅ Path fixed: Direct templates se file uthayega
            String htmlContent = templateEngine.process("order-confirmation", context);

            helper.setFrom("projectabdulbasit09@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Order Confirmed! - Mobile Store");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Order Email Sent Successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("Email Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}