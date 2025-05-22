package com.microservice.BookingService.service;


import com.microservice.BookingService.dto.*;
import com.microservice.BookingService.entity.BookingEntity;
import com.microservice.BookingService.repository.BookingRepository;
import com.microservice.BookingService.utils.Notification;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class BookingService {
    @Autowired
    private static final Logger logger = LogManager.getLogger(BookingService.class);
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private Notification notification;

    @Autowired
    private RestTemplate restTemplate;

    // Method to get all booking from the system
    public List<BookingDTO> getAllBookings() {
        logger.info("ADMIN CALLED GET ALL BOOKING FUNCTION...SUCCESS");
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
                .collect(Collectors.toList());
        // normal modifiable list
    }

    // Method to get booking by id
    public BookingDTO getBookingById(String id) {
        logger.info("get booking by id called");
        BookingDTO dto = null;
        Optional<BookingEntity> book = bookingRepository.findById(id);
        if(book!=null){
            dto = new BookingDTO(book.get().getId(),book.get().getUserId(),book.get().getFlightId(),book.get().getEmail(),book.get().getName(),book.get().getAge(),book.get().getAddress(),book.get().getFromCity(),book.get().getToCity(),book.get().getDeparture(),book.get().getArrival(),book.get().getDepartureTime(),book.get().getArrivalTime(),book.get().getType(),book.get().getPaymentId(),book.get().getPaymentAmount());
        }
        logger.info("retured booking with id : "+book.get().getId());
        return dto;
    }

    // method to update booking by id
    public Map<String,String> updateBookingById(String id, BookingDTO bookingEntity) {
        logger.info("update booking by id called");
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
            logger.info("Booking updated successfully with id: "+id);
            return mp;
        }).orElseGet(() -> {
            mp.put("message","Booking not found with id : "+id);
            logger.warn("Booking not found with id : "+id);
            return mp;
        });
    }

    // Method to delete by id
    public Map<String, String> deleteBookingById(String id){
        logger.info("delete booking by id called");
        Map<String,String> mp = new HashMap<>();
        Optional<BookingEntity> book = bookingRepository.findById(id);
        if(book!=null){
            bookingRepository.deleteById(id);
            mp.put("message","Booking deleted successfully");
            logger.info("Booking deleted successfully");
            return mp;
        }else{
            mp.put("message","Booking with this id not found");
            logger.warn("Booking with this id not found");
            return mp;
        }

    }




    // Method to get payment by user id
    public List<Payment> getBookingsByUserId(String id) {
        logger.info(" Get payment by id called");
        List<Payment> payments = new ArrayList<>();

       bookingRepository.findByUserId(id)
                .forEach(book -> {
                    // Call Payment microservice

                    try {
                        System.out.println("payment entering");
                        Payment payment = restTemplate.getForObject(
                                "http://localhost:8084/product/v1/findBySessionId/" + book.getPaymentId(),
                                Payment.class
                        );

                        if (payment != null) {
                            payments.add(payment);
                        }
                    } catch (Exception e) {
                        // Log and skip failed calls
                        System.out.println("Failed to fetch payment for ID: " + book.getPaymentId());
                    }
                });

       logger.info("returning payment from the system");
        return payments;
    }


    // Method to add new booking manually
    public Map<String, String> addNewBooking(BookingDTO book){
        logger.info("add new booking called");
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
            logger.info("Booking Created successfully");
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


    // Method to cancel booking with id from the system
    public  Map<String, String> cancelByBookingId(String bookingId) {
        logger.info("Cancel booking called");
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
            Msg msg = new Msg();
            msg.setTo(booking.getEmail());
            msg.setSubject("Booking Canceled Successfully");
            String cancelMsg = "<html>" +
                    "<head>" +
                    "<style>" +
                    "    body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; }" +
                    "    .container { max-width: 600px; background-color: #fff; padding: 30px; margin: 20px auto; " +
                    "        border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }" +
                    "    h2 { color: #d32f2f; text-align: center; }" +
                    "    p { font-size: 16px; line-height: 1.5; }" +
                    "    .highlight { color: #d32f2f; font-weight: bold; }" +
                    "    .footer { margin-top: 30px; font-size: 14px; color: #666; text-align: center; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <h2>❌ Booking Canceled</h2>" +
                    "    <p>Your booking from <strong>" + booking.getFromCity() + " → " + booking.getToCity() + "</strong> has been <span class='highlight'>successfully canceled</span>.</p>" +
                    "    <p><strong>Passenger:</strong> " + booking.getName() + " (" + booking.getAge() + " yrs)<br>" +
                    "       <strong>Flight ID:</strong> " + booking.getFlightId() + "<br>" +
                    "       <strong>Departure:</strong> " + booking.getDeparture() + "<br>" +
                    "       <strong>Arrival:</strong> " + booking.getArrival() + "<br>" +
                    "       <strong>Payment ID:</strong> " + booking.getPaymentId() + "<br>" +
                    "       <strong>Amount Paid:</strong> ₹" + booking.getPaymentAmount() +
                    "    </p>" +
                    "    <p>A refund has been <span class='highlight'>initiated</span> and will be credited to your bank account within <strong>7 working days</strong>.</p>" +
                    "    <p class='footer'>Thank you for choosing <strong>Fly-VJADAUN</strong>. We hope to serve you again soon.</p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            msg.setText(cancelMsg);
            notification.send(msg);
            logger.info("Booking canceled with id : " + bookingId);
            return mp;
        } catch (Exception e) {
            System.out.println("Failed to delete payment with ID: " + paymentId + ", Reason: " + e.getMessage());
            mp.put("message","Booking with this id not found");
            logger.warn("Booking not found with this id : " + bookingId);
            return mp;
        }
    }

    // Method for booking ticket
    public StripeResponse book(BookingDTO booking){
        logger.info("booking is called");
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
        Msg msg = new Msg();
        msg.setTo(entity.getEmail());
        msg.setSubject("Your Flight is Successfully Booked. Thank you for using Fly Vjadaun Application");
        String html = """
<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
  <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; text-align: center;">✈️ Booking Confirmation</h2>
    <p>Dear %s,</p>
    <p>Thank you for booking with <strong>VJ-FLY-SYSTEM</strong>. Your booking has been confirmed. Here are the details:</p>

    <table style="width: 100%%; margin-top: 20px;">
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Booking ID:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">User ID:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Flight ID:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Email:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Name:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Age:</td><td style="padding: 8px;">%d</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Address:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">From:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">To:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Departure Date:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Arrival Date:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Departure Time:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Arrival Time:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Flight Type:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Payment ID:</td><td style="padding: 8px;">%s</td></tr>
      <tr><td style="padding: 8px; font-weight: bold; color: #333;">Amount Paid:</td><td style="padding: 8px;">₹%d</td></tr>
    </table>

    <p style="margin-top: 30px;">We wish you a pleasant journey!</p>
    <p style="text-align: center; font-size: 12px; color: #888;">© 2025 VJ-FLY-SYSTEM. All rights reserved.</p>
  </div>
</body>
</html>
""".formatted(
                booking.getName(),
                booking.getId(),
                booking.getUserId(),
                booking.getFlightId(),
                booking.getEmail(),
                booking.getName(),
                booking.getAge(),
                booking.getAddress(),
                booking.getFromCity(),
                booking.getToCity(),
                new SimpleDateFormat("yyyy-MM-dd").format(booking.getDeparture()),
                new SimpleDateFormat("yyyy-MM-dd").format(booking.getArrival()),
                new SimpleDateFormat("HH:mm").format(booking.getDepartureTime()),
                new SimpleDateFormat("HH:mm").format(booking.getArrivalTime()),
                booking.getType(),
                booking.getPaymentId(),
                booking.getPaymentAmount()
        );


        msg.setText(html);
        notification.send(msg);

        logger.info("booking created successfully with id : "+booking.getId());
        return stripeResponse.getBody();
    }




}
