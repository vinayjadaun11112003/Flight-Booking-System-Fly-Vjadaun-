package com.microservice.UserService.utils;

import com.microservice.UserService.dto.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class Notification {

    @Autowired
    private StreamBridge sb;

    public String send(Msg msg) {

        boolean rs = sb.send("notificationEvent-out-0",msg);
        if(rs){
            return "it works";
        }else{
            return "failed";
        }
    }
}
