package com.microservice.SearchFlights;

import com.microservice.SearchFlights.client.CheckinServiceClient;
import com.microservice.SearchFlights.client.FareServiceClient;
import com.microservice.SearchFlights.dto.FareRequest;
import com.microservice.SearchFlights.dto.FlightDTO;
import com.microservice.SearchFlights.dto.SearchFlightRequestDTO;
import com.microservice.SearchFlights.entity.FlightEntity;
import com.microservice.SearchFlights.repository.FlightRepository;
import com.microservice.SearchFlights.service.SearchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FareServiceClient fareServiceClient;

    @Mock
    private CheckinServiceClient checkinServiceClient;

    @InjectMocks
    private SearchService searchService;

    private FlightDTO flightDTO;
    private FlightEntity flightEntity;
    private FareRequest fareRequest;

    @BeforeEach
    public void setUp() {
        fareRequest = new FareRequest("1", 100L, 200L);
        flightDTO = new FlightDTO("1", "Flight 101", "NYC", "LA", new Date(), new Date(), new Date(), new Date(), fareRequest);
        flightEntity = new FlightEntity("1", "Flight 101", "NYC", "LA", new Date(), new Date(), new Date(), new Date(), fareRequest);
    }
    @Test
    public void testGetAllFlights() {
        when(flightRepository.findAll()).thenReturn(Collections.singletonList(flightEntity));
        when(fareServiceClient.getFare(anyString())).thenReturn(fareRequest);

        List<FlightDTO> result = searchService.getAllFlights();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Flight 101", result.get(0).getFlightName());
        verify(flightRepository, times(1)).findAll();
        verify(fareServiceClient, times(1)).getFare(anyString());
    }

    @Test
    public void testUpdateFlight() {
        when(flightRepository.findById(anyString())).thenReturn(Optional.of(flightEntity));
        doNothing().when(fareServiceClient).updateFare(eq("1"), any());

        String result = searchService.updateFlight("1", flightDTO);

        assertEquals("Flight and Fare updated successfully for ID 1", result);
        verify(flightRepository, times(1)).save(any(FlightEntity.class));
        verify(fareServiceClient, times(1)).updateFare(eq("1"), any());
    }

    @Test
    public void testGetFlightById() {
        when(flightRepository.findById(anyString())).thenReturn(Optional.of(flightEntity));
        when(fareServiceClient.getFare(anyString())).thenReturn(fareRequest);

        FlightDTO result = searchService.getFlightById("1");

        assertNotNull(result);
        assertEquals("Flight 101", result.getFlightName());
        verify(flightRepository, times(1)).findById(anyString());
        verify(fareServiceClient, times(1)).getFare(anyString());
    }

    @Test
    public void testDeleteFlight() {
        when(flightRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(flightRepository).deleteById(anyString());
        doNothing().when(fareServiceClient).deleteFare(anyString());

        String result = searchService.deleteFlight("1");

        assertEquals("Flight with ID 1 and associated fare deleted successfully.", result);
        verify(flightRepository, times(1)).deleteById(anyString());
        verify(fareServiceClient, times(1)).deleteFare(anyString());
    }

    @Test
    public void testSearchFlights() {
        SearchFlightRequestDTO request = new SearchFlightRequestDTO("NYC", "LA", new Date());
        when(flightRepository.findByFromCityAndToCityAndDepartureDate(anyString(), anyString(), any())).thenReturn(Collections.singletonList(flightEntity));
        when(fareServiceClient.getFare(anyString())).thenReturn(fareRequest);

        List<FlightDTO> result = searchService.searchFlights(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Flight 101", result.get(0).getFlightName());
        verify(flightRepository, times(1)).findByFromCityAndToCityAndDepartureDate(anyString(), anyString(), any());
        verify(fareServiceClient, times(1)).getFare(anyString());
    }
}
