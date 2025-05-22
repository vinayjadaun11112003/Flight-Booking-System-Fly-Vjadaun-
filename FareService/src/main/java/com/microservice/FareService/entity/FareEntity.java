package com.microservice.FareService.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
@Document(collection="fares")
public class FareEntity {
    @Id
    private String flightId;
    private Long economyFare;
    private Long businessFare;
}
