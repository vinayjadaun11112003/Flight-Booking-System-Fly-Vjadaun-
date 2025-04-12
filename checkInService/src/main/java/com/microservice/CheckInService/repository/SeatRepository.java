package com.microservice.CheckInService.repository;

import com.microservice.CheckInService.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightId(String flightId);

    Optional<Seat> findByFlightIdAndSeatNumber(String flightId, String seatNumber);
    List<Seat> findByPassengerId(String passengerId);
    List<Seat> findByFlightIdAndIsBookedFalse(String flightId);

    List<Seat> findByFlightIdAndPassengerIdIsNull(String flightId);
}
