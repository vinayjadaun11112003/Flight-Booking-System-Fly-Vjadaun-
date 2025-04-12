package com.microservice.SearchFlights.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchFlightRequestDTO {
    private String fromCity;
    private String toCity;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date departureDate;
}
