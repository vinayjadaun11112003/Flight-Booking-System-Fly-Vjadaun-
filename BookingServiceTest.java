package com.microservice.BookingService;

import com.microservice.BookingService.client.PaymentServiceClient;
import com.microservice.BookingService.dto.*;
import com.microservice.BookingService.entity.BookingEntity;
import com.microservice.BookingService.repository.BookingRepository;
import com.microservice.BookingService.service.BookingService;
import com.microservice.BookingService.utils.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private Notification notification;

    @Mock
    private PaymentServiceClient paymentServiceClient; // ✅ Replaces RestTemplate

    @InjectMocks
    private BookingService bookingService;

    private BookingDTO bookingDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDTO = new BookingDTO(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );
    }

    @Test
    public void testGetAllBookings() {
        BookingEntity bookingEntity = new BookingEntity(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );

        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(bookingEntity));

        List<BookingDTO> bookings = bookingService.getAllBookings();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getName());
    }

    @Test
    public void testGetBookingById() {
        BookingEntity bookingEntity = new BookingEntity(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );

        when(bookingRepository.findById("1")).thenReturn(Optional.of(bookingEntity));

        BookingDTO booking = bookingService.getBookingById("1");
        assertNotNull(booking);
        assertEquals("John Doe", booking.getName());
    }

    @Test
    public void testUpdateBookingById() {
        BookingEntity bookingEntity = new BookingEntity(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );

        when(bookingRepository.findById("1")).thenReturn(Optional.of(bookingEntity));

        Map<String, String> response = bookingService.updateBookingById("1", bookingDTO);
        assertNotNull(response);
        assertEquals("Booking updated successfully with id: 1", response.get("message"));
    }

    @Test
    public void testDeleteBookingById() {
        BookingEntity bookingEntity = new BookingEntity(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );

        when(bookingRepository.findById("1")).thenReturn(Optional.of(bookingEntity));

        Map<String, String> response = bookingService.deleteBookingById("1");
        assertNotNull(response);
        assertEquals("Booking deleted successfully", response.get("message"));
    }

    @Test
    public void testAddNewBooking() {
        // Mocking Stripe payment response
        StripeResponse stripeResponse = new StripeResponse();
        stripeResponse.setStatus("SUCCESS");
        stripeResponse.setSessionId("session_test");

        when(paymentServiceClient.checkout(any(ProductRequest.class))).thenReturn(stripeResponse);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(new BookingEntity());

        Map<String, String> response = bookingService.addNewBooking(bookingDTO);
        assertNotNull(response);
        assertEquals("Booking Created successfully", response.get("message"));
    }

    @Test
    public void testGetBookingsByUserId() {
        BookingEntity bookingEntity = new BookingEntity(
                "1", "user1", "flight1", "user1@example.com", "John Doe", 25, "Address",
                "City1", "City2", new Date(), new Date(), new Date(), new Date(),
                "Economy", "payment1", 2000L
        );

        when(bookingRepository.findByUserId("user1")).thenReturn(Collections.singletonList(bookingEntity));

        List<Payment> payments = bookingService.getBookingsByUserId("user1");
        assertNotNull(payments);
        assertEquals(0, payments.size()); // Assuming actual logic for conversion not added
    }
}
