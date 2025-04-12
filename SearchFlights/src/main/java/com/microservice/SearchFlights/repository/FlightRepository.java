package com.microservice.SearchFlights.repository;

import com.microservice.SearchFlights.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, String> {
    List<FlightEntity> findByFromCityAndToCityAndDepartureDate(String fromCity, String toCity, Date departureDate);
}
