package com.microservice.SearchFlights.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FareRequest {
    private String flightId;
    private Long economyFare;
    private Long businessFare;

    // getters and setters
}
