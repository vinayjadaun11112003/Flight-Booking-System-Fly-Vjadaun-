package com.microservice.Notification.dto;

import lombok.Data;

@Data
public class RequestMail {
    String to;
    String subject;
    String text;

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public String getSubject() {
        return subject;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
