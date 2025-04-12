package com.microservice.BookingService.service;


import com.microservice.BookingService.dto.BookingDTO;
import com.microservice.BookingService.dto.Payment;
import com.microservice.BookingService.dto.ProductRequest;
import com.microservice.BookingService.dto.StripeResponse;
import com.microservice.BookingService.entity.BookingEntity;
import com.microservice.BookingService.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(book -> new BookingDTO(
                        book.getId(),
                        book.getUserId(),
                        book.getFlightId(),
                        book.getEmail(),
                        book.getName(),
                        book.getAge(),
                        book.getAddress(),
                        book.getFromCity(),
                        book.getToCity(),
                        book.getDeparture(),
                        book.getArrival(),
                        book.getDepartureTime(),
                        book.getArrivalTime(),
                        book.getType(),
                        book.getPaymentId(),
                        book.getPaymentAmount()
                ))
                .collect(Collectors.toList()); // normal modifiable list
    }


    public BookingDTO getBookingById(String id) {
        BookingDTO dto = null;
        Optional<BookingEntity> book = bookingRepository.findById(id);
        if(book!=null){
            dto = new BookingDTO(book.get().getId(),book.get().getUserId(),book.get().getFlightId(),book.get().getEmail(),book.get().getName(),book.get().getAge(),book.get().getAddress(),book.get().getFromCity(),book.get().getToCity(),book.get().getDeparture(),book.get().getArrival(),book.get().getDepartureTime(),book.get().getArrivalTime(),book.get().getType(),book.get().getPaymentId(),book.get().getPaymentAmount());
        }
        return dto;
    }

    public Map<String,String> updateBookingById(String id, BookingDTO bookingEntity) {
        Map<String,String> mp = new HashMap<>();
      return  bookingRepository.findById(id).map(booking -> {
            booking.setUserId(bookingEntity.getUserId());
            booking.setFlightId(bookingEntity.getFlightId());
            booking.setEmail(bookingEntity.getEmail());
            booking.setAge(bookingEntity.getAge());
            booking.setName(bookingEntity.getName());
            booking.setAddress(bookingEntity.getAddress());
            booking.setFromCity(bookingEntity.getFromCity());
            booking.setToCity(bookingEntity.getToCity());
            booking.setDeparture(bookingEntity.getDeparture());
            booking.setArrival(bookingEntity.getArrival());
            booking.setDepartureTime(bookingEntity.getDepartureTime());
            booking.setArrivalTime(bookingEntity.getArrivalTime());
            booking.setType(bookingEntity.getType());
            booking.setPaymentId(bookingEntity.getPaymentId());
            booking.setPaymentAmount(bookingEntity.getPaymentAmount());
            bookingRepository.save(booking);
            mp.put("message","Booking updated successfully with id: "+id);
            return mp;
        }).orElseGet(() -> {
            mp.put("message","Booking not found with id : "+id);
            return mp;
        });
    }

    public Map<String, String> deleteBookingById(String id){
        Map<String,String> mp = new HashMap<>();
        Optional<BookingEntity> book = bookingRepository.findById(id);
        if(book!=null){
            bookingRepository.deleteById(id);
            mp.put("message","Booking deleted successfully");
            return mp;
        }else{
            mp.put("message","Booking with this id not found");
            return mp;
        }

    }




    public List<Payment> getBookingsByUserId(String id) {
        List<Payment> payments = new ArrayList<>();

       bookingRepository.findByUserId(id)
                .forEach(book -> {
                    // Call Payment microservice
                    System.out.println("hello");
                    try {
                        System.out.println("payment entering");
                        Payment payment = restTemplate.getForObject(
                                "http://localhost:8084/product/v1/findBySessionId/" + book.getPaymentId(),
                                Payment.class
                        );
                        System.out.println("payment exiting");
                        if (payment != null) {
                            payments.add(payment);
                        }
                    } catch (Exception e) {
                        // Log and skip failed calls
                        System.out.println("Failed to fetch payment for ID: " + book.getPaymentId());
                    }
                });
//        System.out.println(bookingEntities.toString());
        System.out.println("map payment"+payments.toString());
        return payments;
    }


    public Map<String, String> addNewBooking(BookingDTO book){
        Map<String,String> mp = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        book.setId(uuid.toString());
        BookingEntity booking = new BookingEntity(
                book.getId(),
                book.getUserId(),
                book.getFlightId(),
                book.getEmail(),
                book.getName(),
                book.getAge(),
                book.getAddress(),
                book.getFromCity(),
                book.getToCity(),
                book.getDeparture(),
                book.getArrival(),
                book.getDepartureTime(),
                book.getArrivalTime(),
                book.getType(),
                book.getPaymentId(),
                book.getPaymentAmount()
        );
            bookingRepository.save(booking);
            mp.put("message","Booking Created successfully");
            return mp;


    }

    public  Map<String, String> deleteByBookingId(String bookingId) {
        Map<String,String> mp = new HashMap<>();
        // Step 1: Retrieve booking by ID
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // Step 2: Get payment ID from the booking
        String paymentId = booking.getPaymentId();

        // Step 3: Delete the booking
        bookingRepository.deleteById(bookingId);

        // Step 4: Call Payment service to delete the payment
        String paymentServiceUrl = "http://localhost:8084/product/v1/deleteBySessionId/" + paymentId; // replace with actual service port

        try {
            restTemplate.delete(paymentServiceUrl);
            mp.put("message","Booking deleted successfully");
            return mp;
        } catch (Exception e) {
            System.out.println("Failed to delete payment with ID: " + paymentId + ", Reason: " + e.getMessage());
            mp.put("message","Booking with this id not found");
            return mp;
        }
    }


    public StripeResponse book(BookingDTO booking){
        UUID uuid = UUID.randomUUID();
        booking.setId(uuid.toString());

        // Prepare Stripe request payload
        ProductRequest productRequest = new ProductRequest();
        productRequest.setUserId(23L);
        productRequest.setAmount(booking.getPaymentAmount());
        productRequest.setQuantity(1L);
        productRequest.setName("Flight Booking Payment");
        productRequest.setCurrency("INR");

        // Call Stripe Service
        String stripeUrl = "http://localhost:8084/product/v1/checkout";
        ResponseEntity<StripeResponse> stripeResponse = restTemplate.postForEntity(stripeUrl, productRequest, StripeResponse.class);

        // Set paymentId in booking entity
        if (stripeResponse.getBody() != null && "SUCCESS".equalsIgnoreCase(stripeResponse.getBody().getStatus())) {
            booking.setPaymentId(stripeResponse.getBody().getSessionId());
        }
        BookingEntity entity = new BookingEntity(booking.getId(),booking.getUserId(),booking.getFlightId(),booking.getEmail(),booking.getName(),booking.getAge(),booking.getAddress(),booking.getFromCity(),booking.getToCity(),booking.getDeparture(),booking.getArrival(),booking.getDepartureTime(),booking.getArrivalTime(),booking.getType(), booking.getPaymentId(), booking.getPaymentAmount());
        bookingRepository.save(entity);
        System.out.println(stripeResponse.getBody().getSessionId());
        return stripeResponse.getBody();
    }
}
