package com.microservice.CheckInService;

import com.microservice.CheckInService.dto.Msg;
import com.microservice.CheckInService.entity.Seat;
import com.microservice.CheckInService.repository.SeatRepository;
import com.microservice.CheckInService.service.CheckinService;
import com.microservice.CheckInService.utils.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckinServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private Notification notification;

    @InjectMocks
    private CheckinService checkinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitializeSeats_shouldSaveAllSeats() {
        String flightId = "FL123";
        int totalSeats = 8;

        String result = checkinService.initializeSeats(flightId, totalSeats);

        ArgumentCaptor<List<Seat>> captor = ArgumentCaptor.forClass(List.class);
        verify(seatRepository, times(1)).saveAll(captor.capture());

        List<Seat> savedSeats = captor.getValue();
        assertEquals(8, savedSeats.size());
        assertEquals("1A", savedSeats.get(0).getSeatNumber());
        assertEquals("2D", savedSeats.get(7).getSeatNumber());
        assertEquals("Seats initiliazed", result);
    }

    @Test
    void testCheckin_shouldReturnAlreadyCheckedIn() {
        String passengerId = "P123";
        String flightId = "FL123";
        String seatNumber = "1A";
        String email = "test@example.com";

        Seat seat = new Seat();
        seat.setSeatNumber("1A");
        seat.setPassengerId(passengerId);
        seat.setBooked(true);

        when(seatRepository.findByPassengerId(passengerId)).thenReturn(List.of(seat));

        String result = checkinService.checkin(flightId, passengerId, seatNumber, email);

        assertTrue(result.contains("Already checked in"));
    }

    @Test
    void testCheckin_seatAlreadyBooked_shouldReturnMessage() {
        String flightId = "FL123";
        String seatNumber = "1A";
        String passengerId = "P123";
        String email = "test@example.com";

        Seat seat = new Seat();
        seat.setSeatNumber(seatNumber);
        seat.setBooked(true);

        when(seatRepository.findByPassengerId(passengerId)).thenReturn(Collections.emptyList());
        when(seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)).thenReturn(Optional.of(seat));

        String result = checkinService.checkin(flightId, passengerId, seatNumber, email);

        assertEquals("Seat already booked. Please select another seat.", result);
    }

    @Test
    void testCheckin_shouldSucceedAndSendNotification() {
        String flightId = "FL123";
        String seatNumber = "1A";
        String passengerId = "P123";
        String email = "test@example.com";

        Seat seat = new Seat();
        seat.setSeatNumber(seatNumber);
        seat.setBooked(false);

        when(seatRepository.findByPassengerId(passengerId)).thenReturn(Collections.emptyList());
        when(seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)).thenReturn(Optional.of(seat));

        String result = checkinService.checkin(flightId, passengerId, seatNumber, email);

        assertEquals("Checked in successfully!", result);
        assertTrue(seat.isBooked());
        assertEquals(passengerId, seat.getPassengerId());
        assertEquals(email, seat.getEmail());
        verify(seatRepository, times(1)).save(seat);
        verify(notification, times(1)).send(any(Msg.class));
    }
}
