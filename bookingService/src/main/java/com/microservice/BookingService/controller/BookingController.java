package com.microservice.BookingService.controller;

import com.microservice.BookingService.dto.BookingDTO;
import com.microservice.BookingService.dto.Payment;
import com.microservice.BookingService.dto.ProductRequest;
import com.microservice.BookingService.dto.StripeResponse;
import com.microservice.BookingService.entity.BookingEntity;
import com.microservice.BookingService.repository.BookingRepository;
import com.microservice.BookingService.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/BookingService")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    @GetMapping("/test")
    public String test() {
        return "Booking service Working";
    }

    @PostMapping("/addNewBooking")
    public StripeResponse addNewBooking(@RequestBody BookingEntity booking) {
        BookingDTO dto = new BookingDTO(booking.getId(),booking.getUserId(),booking.getFlightId(),booking.getEmail(),booking.getName(),booking.getAge(),booking.getAddress(),booking.getFromCity(),booking.getToCity(),booking.getDeparture(),booking.getArrival(),booking.getDepartureTime(),booking.getArrivalTime(),booking.getType(), booking.getPaymentId(), booking.getPaymentAmount());
        return bookingService.book(dto);
    }
    @GetMapping("/getAllBookings")
    public List<BookingDTO> getAllBookings(){
        return bookingService.getAllBookings();
    }

    @GetMapping("/getPayments/{id}")
    public List<Payment> getBookingsByUserId(@PathVariable String id){
        return bookingService.getBookingsByUserId(id);
    }
}
