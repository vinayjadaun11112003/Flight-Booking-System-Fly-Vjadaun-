package com.microservice.BookingService.dto;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String status;
    private String currency;
    private Long amount;
    private LocalDateTime createdAt;

    public Payment() {
        this.createdAt = LocalDateTime.now();
    }

    public Payment(String sessionId, String status, String currency, Long amount) {
        this.sessionId = sessionId;
        this.status = status;
        this.currency = currency;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public String getStatus() { return status; }
    public String getCurrency() { return currency; }
    public Long getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setAmount(Long amount) { this.amount = amount; }
}
