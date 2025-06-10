package com.microservice.BookingService.client;


import com.microservice.BookingService.dto.Payment;
import com.microservice.BookingService.dto.ProductRequest;
import com.microservice.BookingService.dto.StripeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE", url = "http://localhost:8084") // Change to your Eureka name if using
public interface PaymentServiceClient {

    @GetMapping("/product/v1/findBySessionId/{sessionId}")
    Payment getPaymentBySessionId(@PathVariable String sessionId);

    @DeleteMapping("/product/v1/deleteBySessionId/{sessionId}")
    void deletePaymentBySessionId(@PathVariable String sessionId);

    @PostMapping("/product/v1/checkout")
    StripeResponse checkout(@RequestBody ProductRequest request);
}
