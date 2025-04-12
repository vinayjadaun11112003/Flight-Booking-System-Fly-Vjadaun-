package com.microservice.Notification.functions;

import com.microservice.Notification.controller.NotificationController;
import com.microservice.Notification.dto.RequestMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class Notification {
    @Autowired
    private NotificationController n;

    @Bean
    public Function<RequestMail,String> sendMsg(){
        return (r) -> {
            System.out.println(r.getTo());
            System.out.println(r.getSubject());
            System.out.println(r.getText());
            String s = n.sendEmail(r);
            System.out.println(s);
            return "done";
        };
    }
}
