package com.microservice.CheckInService.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeatDTO {
    private String seatId;
    private String flightId;
    private String seatNumber;
    private boolean isBooked;
    private String passengerId;
    private String email;
}
