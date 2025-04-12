package com.microservice.Notification.controller;

import com.microservice.Notification.dto.RequestMail;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RestController
@RequestMapping("/home")
public class NotificationController {

    @GetMapping("hello")
    public String hello(){
        return "hello world";
    }


    private final JavaMailSender mailSender;
    @Autowired
    private NotificationController(JavaMailSender s){
        this.mailSender=s;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody RequestMail mail) {
        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("udit0428t@gmail.com"); // Sender's email
//            message.setTo(mail.getTo()); // Recipient's email
//            message.setSubject(mail.getSubject());
//            message.setText(mail.getText());
            MimeMessage message = mailSender.createMimeMessage();
              MimeMessageHelper helper = new MimeMessageHelper(message, true); // `true` enables multipart

               helper.setTo(mail.getTo());
               helper.setSubject(mail.getSubject());
               helper.setText(mail.getText(), true);

               mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}
