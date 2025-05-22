package com.microservice.SearchFlights;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SearchService searchService;

    private FlightDTO flightDTO;
    private FlightEntity flightEntity;
    private FareRequest fareRequest;

    @BeforeEach
    public void setUp() {
        flightDTO = new FlightDTO("1", "Flight 101", "NYC", "LA", new Date(), new Date(), new Date(), new Date(), new FareRequest("1",100L, 200L));
        flightEntity = new FlightEntity("1", "Flight 101", "NYC", "LA", new Date(), new Date(), new Date(), new Date(), fareRequest);
        fareRequest = new FareRequest("1",100L, 200L);
    }

    @Test
    public void testAddFlight() {
        // Arrange: Mock dependencies
        when(flightRepository.save(any(FlightEntity.class))).thenReturn(flightEntity);

        // Mock the restTemplate POST request for creating seats (does not return anything)
        doAnswer(invocation -> null) // Simulate no response (equivalent to Void.class)
                .when(restTemplate)
                .postForObject(eq("http://localhost:8087/CheckinService/createSeats/{flightId}/{totalSeats}"),
                        any(), eq(Void.class), eq(flightEntity.getFlightId()), eq(40));

        // Mock the restTemplate POST request for adding new fare (returns a FareRequest object)
        FareRequest fareRequestResponse = new FareRequest("1", 100L, 200L); // Example fare response
        when(restTemplate.postForObject(eq("http://localhost:8081/FareService/addNewFare"),
                any(), eq(FareRequest.class)))
                .thenReturn(fareRequestResponse);

        // Act: Call addFlight
        FlightDTO result = searchService.addFlight(flightDTO);

        // Assert: Verify that the correct method was called
        assertNotNull(result);
        assertEquals("Flight 101", result.getFlightName());

        // Verify interactions
        verify(flightRepository, times(1)).save(any(FlightEntity.class));
        verify(restTemplate, times(1))
                .postForObject(eq("http://localhost:8087/CheckinService/createSeats/{flightId}/{totalSeats}"),
                        any(), eq(Void.class), eq(flightEntity.getFlightId()), eq(40));
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:8081/FareService/addNewFare"),
                any(), eq(FareRequest.class));
    }

    @Test
    public void testGetAllFlights() {
        // Arrange: Mock dependencies
        when(flightRepository.findAll()).thenReturn(Collections.singletonList(flightEntity));
        when(restTemplate.getForObject(anyString(), eq(FareRequest.class))).thenReturn(fareRequest);

        // Act: Call getAllFlights
        List<FlightDTO> result = searchService.getAllFlights();

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Flight 101", result.get(0).getFlightName());
        verify(flightRepository, times(1)).findAll();
        verify(restTemplate, times(1)).getForObject(anyString(), eq(FareRequest.class));
    }

    @Test
    public void testUpdateFlight() {
        // Arrange: Mock dependencies
        when(flightRepository.findById(anyString())).thenReturn(Optional.of(flightEntity));
        doNothing().when(restTemplate).put(anyString(), any());

        // Act: Call updateFlight
        String result = searchService.updateFlight("1", flightDTO);

        // Assert: Verify the result
        assertEquals("Flight and Fare updated successfully for ID 1", result);
        verify(flightRepository, times(1)).save(any(FlightEntity.class));
        verify(restTemplate, times(1)).put(anyString(), any());
    }

    @Test
    public void testGetFlightById() {
        // Arrange: Mock dependencies
        when(flightRepository.findById(anyString())).thenReturn(Optional.of(flightEntity));
        when(restTemplate.getForObject(anyString(), eq(FareRequest.class))).thenReturn(fareRequest);

        // Act: Call getFlightById
        FlightDTO result = searchService.getFlightById("1");

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals("Flight 101", result.getFlightName());
        verify(flightRepository, times(1)).findById(anyString());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(FareRequest.class));
    }

    @Test
    public void testDeleteFlight() {
        // Arrange: Mock dependencies
        when(flightRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(flightRepository).deleteById(anyString());
        doNothing().when(restTemplate).delete(anyString());

        // Act: Call deleteFlight
        String result = searchService.deleteFlight("1");

        // Assert: Verify the result
        assertEquals("Flight with ID 1 and associated fare deleted successfully.", result);
        verify(flightRepository, times(1)).deleteById(anyString());
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    public void testSearchFlights() {
        // Arrange: Mock dependencies
        SearchFlightRequestDTO request = new SearchFlightRequestDTO("NYC", "LA", new Date());
        when(flightRepository.findByFromCityAndToCityAndDepartureDate(anyString(), anyString(), any())).thenReturn(Collections.singletonList(flightEntity));

        // Act: Call searchFlights
        List<FlightDTO> result = searchService.searchFlights(request);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Flight 101", result.get(0).getFlightName());
        verify(flightRepository, times(1)).findByFromCityAndToCityAndDepartureDate(anyString(), anyString(), any());
    }
}
