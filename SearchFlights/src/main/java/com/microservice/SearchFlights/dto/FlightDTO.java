package com.microservice.SearchFlights.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Transient;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightDTO {
    private String flightId;
    private String flightName;
    private String fromCity;
    private String toCity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date departureDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date arrivalDate;

    @JsonFormat(pattern = "HH:mm")
    private Date departureTime;


    @JsonFormat(pattern = "HH:mm")
    private Date arrivalTime;

    @Transient
    private FareRequest fareRequest;
}
