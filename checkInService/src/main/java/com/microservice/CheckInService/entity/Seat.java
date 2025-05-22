package com.microservice.CheckInService.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "checkin")
public class Seat {

    @Id
    private String seatId;
    private String flightId;
    private String seatNumber;
    private boolean isBooked;
    private String passengerId;
    private String email;// Null if not booked

    // Getters and Setters
}
