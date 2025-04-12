package com.microservice.FareService.dto;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FareDTO {
    private String flightId;
    private Long economyFare;
    private Long businessFare;
}
