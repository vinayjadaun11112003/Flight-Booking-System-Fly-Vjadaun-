package com.microservice.CheckInService.controller;
import com.microservice.CheckInService.dto.SeatDTO;
import com.microservice.CheckInService.entity.Seat;
import com.microservice.CheckInService.repository.SeatRepository;
import com.microservice.CheckInService.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/CheckinService")
public class CheckInController {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CheckinService checkinService;
    @PostMapping("/createSeats/{flightId}/{totalSeats}")
    public void initializeSeatsForFlight(@PathVariable String flightId, @PathVariable int totalSeats) {
        String result = checkinService.initializeSeats(flightId,totalSeats);
        System.out.println(result);
    }



    @PostMapping("/CheckIn/{flightId}/{passengerId}/{seatNumber}")
    public String checkIn(@PathVariable String flightId,
                          @PathVariable String passengerId,
                          @PathVariable String seatNumber) {
       return checkinService.checkin(flightId,passengerId,seatNumber);
    }



    @GetMapping("CheckSeats/{flightId}")
    public List<SeatDTO> getUnassignedSeats(@PathVariable String flightId) {
        // Step 1: Fetch all seats for the flight
        List<Seat> allSeats = seatRepository.findByFlightId(flightId);


        // Step 2: Filter out the ones where passengerId is null
        List<Seat> unassignedSeats = allSeats.stream()
                .filter(seat -> seat.isBooked() == false)
                .collect(Collectors.toList());


        List<SeatDTO> dto = new ArrayList<>();
        for(Seat seat : unassignedSeats){
            SeatDTO d = new SeatDTO(seat.getSeatId(),seat.getFlightId(),seat.getSeatNumber(),seat.isBooked(),seat.getPassengerId());
            dto.add(d);
        }
        return dto;
    }

}
