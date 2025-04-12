package com.microservice.BookingService.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservice.BookingService.dto.Payment;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.websocket.server.ServerEndpoint;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="Bookings")
public class BookingEntity {
    @Id
    private String Id;
    private String userId;
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
