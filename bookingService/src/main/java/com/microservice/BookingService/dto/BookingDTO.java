package com.microservice.BookingService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Transient;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDTO {
    private String Id;
    private String UserId;
    private String flightId;
    private String email;
    private String name;
    private int age;
    private String address;
    private String fromCity;
    private String toCity;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date departure;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date arrival;

    @JsonFormat(pattern = "HH:mm")
    private Date departureTime;

    @JsonFormat(pattern = "HH:mm")
    private Date arrivalTime;
    private String type;
    private String paymentId;
    private Long paymentAmount;

}
