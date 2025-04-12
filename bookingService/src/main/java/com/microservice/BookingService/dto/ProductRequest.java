package com.microservice.BookingService.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long userId;
    // Getters

    private Long amount;

    private Long quantity;

    private String name;

    private String currency;

}
