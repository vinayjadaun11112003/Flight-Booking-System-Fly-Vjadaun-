package com.microservice.CheckInService.service;
import com.microservice.CheckInService.dto.Msg;
import com.microservice.CheckInService.entity.Seat;
import com.microservice.CheckInService.repository.SeatRepository;
import com.microservice.CheckInService.utils.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckinService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private Notification notification;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(CheckinService.class);

    // Method to initialize seat
       public String initializeSeats(String flightId,int totalSeats){
           logger.info("Seat Initialized for flight id : "+ flightId);
           List<Seat> seats = new ArrayList<>();

           for (int i = 1; i <= totalSeats; i++) {
               String seatNumber = generateSeatNumber(i);  // Example: 1A, 1B, 1C, etc.

               Seat seat = new Seat();
               UUID uuid = UUID.randomUUID();
               seat.setSeatId(uuid.toString());
               seat.setFlightId(flightId);
               seat.setSeatNumber(seatNumber);
               seat.setBooked(false);   // Initially available
               seat.setPassengerId(null);
               seat.setEmail(null);
               seats.add(seat);
           }

           seatRepository.saveAll(seats);
           return "Seats initiliazed";
       }

       // Method for generating seat number
    private String generateSeatNumber(int index) {
           logger.info("Seating generated");
        int row = (index - 1) / 4 + 1;  // Assuming 4 seats per row (A, B, C, D)
        char col = (char) ('A' + (index - 1) % 4);
        return row + "" + col;
    }

    // Method for checkin
    public String checkin(String flightId,String passengerId,String seatNumber,String email){
           logger.info("check - In");
        List<Seat> existingCheckIn = seatRepository.findByPassengerId(passengerId);

        if (!existingCheckIn.isEmpty()) {
            logger.info( "Already checked in with booking ID: " + passengerId +
                    " on seat " + existingCheckIn.get(0).getSeatNumber());
            return "Already checked in with booking ID: " + passengerId +
                    " on seat " + existingCheckIn.get(0).getSeatNumber();

        }
        Optional<Seat> optionalSeat = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber);

        if (!optionalSeat.isPresent() || optionalSeat.get().isBooked()) {
            logger.info("Seat already booked. Please select another seat.");
            return "Seat already booked. Please select another seat.";
        }

        Seat seat = optionalSeat.get();
        seat.setBooked(true);
        seat.setPassengerId(passengerId);
        seat.setEmail(email);
        seatRepository.save(seat);
        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; " +
                "border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;'>" +

                "<h2 style='color: #2E86C1; text-align: center;'>✈️ Check-In Confirmation</h2>" +

                "<p><strong>👤 Passenger:</strong> <span style='color: #34495E;'>" + passengerId + "</span></p>" +

                "<p><strong>🪪 Flight ID:</strong> <span style='display: inline-block; padding: 6px 12px; " +
                "background-color: #D6EAF8; border-radius: 6px; color: #154360; font-weight: bold;'>" + flightId + "</span></p>" +

                "<p><strong>💺 Seat No:</strong> <span style='color: #27AE60; font-weight: bold;'>" + seatNumber + "</span></p>" +

                "<p><strong>📌 Status:</strong> <span style='color: green; font-weight: bold;'>Checked In</span></p>" +

                "<hr style='margin: 20px 0;'/>" +

                "<p style='text-align: center; color: #555;'>Thank you for choosing <strong style='color: #2E86C1;'>Fly-VJADAUN</strong>! ✈️</p>" +
                "</div>";

        Msg msg = new Msg();
        msg.setTo(email);
        msg.setSubject("Thank you for using FLY_VJADAUN [check in successfull]");
        msg.setText(body);
        notification.send(msg);
        logger.info("Checked in successfully!");
        return "Checked in successfully!";
    }

}
