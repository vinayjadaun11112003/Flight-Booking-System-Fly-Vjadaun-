package com.microservice.CheckInService.service;
import com.microservice.CheckInService.entity.Seat;
import com.microservice.CheckInService.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CheckinService {
    @Autowired
    private SeatRepository seatRepository;


       public String initializeSeats(String flightId,int totalSeats){
           List<Seat> seats = new ArrayList<>();

           for (int i = 1; i <= totalSeats; i++) {
               String seatNumber = generateSeatNumber(i);  // Example: 1A, 1B, 1C, etc.

               Seat seat = new Seat();
               seat.setFlightId(flightId);
               seat.setSeatNumber(seatNumber);
               seat.setBooked(false);   // Initially available
               seat.setPassengerId(null);

               seats.add(seat);
           }

           seatRepository.saveAll(seats);
           return "Seats initiliazed";
       }

    private String generateSeatNumber(int index) {
        int row = (index - 1) / 4 + 1;  // Assuming 4 seats per row (A, B, C, D)
        char col = (char) ('A' + (index - 1) % 4);
        return row + "" + col;
    }

    public String checkin(String flightId,String passengerId,String seatNumber){
        List<Seat> existingCheckIn = seatRepository.findByPassengerId(passengerId);

        if (!existingCheckIn.isEmpty()) {
            return "Already checked in with booking ID: " + passengerId +
                    " on seat " + existingCheckIn.get(0).getSeatNumber();
        }
        Optional<Seat> optionalSeat = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber);

        if (!optionalSeat.isPresent() || optionalSeat.get().isBooked()) {
            return "Seat already booked. Please select another seat.";
        }

        Seat seat = optionalSeat.get();
        seat.setBooked(true);
        seat.setPassengerId(passengerId);
        seatRepository.save(seat);

        return "Checked in successfully!";
    }

}
