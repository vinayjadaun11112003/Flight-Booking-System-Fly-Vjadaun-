package com.microservice.Notification.functions;

import com.microservice.Notification.controller.NotificationController;
import com.microservice.Notification.dto.RequestMail;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    private static Logger logger = LoggerFactory.getLogger(Notification.class);

    // Method to send email [VJ]
    @Bean
    public Function<RequestMail,String> sendMsg(){
        return (r) -> {
            logger.info("Email sent Successfully to the mail id : "+r.getTo());
            String info = n.sendEmail(r);
            logger.info(info);
            return "done";
        };
    }
}
