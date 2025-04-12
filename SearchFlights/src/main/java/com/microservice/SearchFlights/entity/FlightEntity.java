package com.microservice.SearchFlights.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservice.SearchFlights.dto.FareRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flights")
public class FlightEntity {
    @Id
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
