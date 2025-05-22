package com.microservice.FareService;

import com.microservice.FareService.dto.FareDTO;
import com.microservice.FareService.entity.FareEntity;
import com.microservice.FareService.repository.FareRepository;
import com.microservice.FareService.service.FareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FareServiceTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private FareService fareService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveFare() {
        FareDTO dto = new FareDTO("FL123", 5000L, 10000L);
        FareEntity savedEntity = new FareEntity("FL123", 5000L, 10000L);

        when(fareRepository.save(any(FareEntity.class))).thenReturn(savedEntity);

        FareDTO result = fareService.saveFare(dto);

        assertNotNull(result);
        assertEquals(dto.getFlightId(), result.getFlightId());
        assertEquals(dto.getEconomyFare(), result.getEconomyFare());
        assertEquals(dto.getBusinessFare(), result.getBusinessFare());

        verify(fareRepository, times(1)).save(any(FareEntity.class));
    }

    @Test
    void testGetFareFound() {
        FareEntity fareEntity = new FareEntity("FL456", 4500L, 8500L);
        when(fareRepository.findById("FL456")).thenReturn(Optional.of(fareEntity));

        FareDTO result = fareService.getFare("FL456");

        assertNotNull(result);
        assertEquals("FL456", result.getFlightId());
        assertEquals(4500L, result.getEconomyFare());
        assertEquals(8500L, result.getBusinessFare());
    }

    @Test
    void testGetFareNotFound() {
        when(fareRepository.findById("FL999")).thenReturn(Optional.empty());

        FareDTO result = fareService.getFare("FL999");

        assertNull(result);
    }

    @Test
    void testUpdateFareSuccess() {
        String id = "FL789";
        FareEntity existing = new FareEntity("FL789", 3000L, 7000L);
        FareDTO updated = new FareDTO("FL789", 3500L, 7500L);

        when(fareRepository.findById(id)).thenReturn(Optional.of(existing));
        when(fareRepository.save(any(FareEntity.class))).thenReturn(existing);

        String result = fareService.update(id, updated);

        assertEquals("Fare updated successfully for ID FL789", result);
        verify(fareRepository).save(any(FareEntity.class));
    }

    @Test
    void testUpdateFareNotFound() {
        when(fareRepository.findById("FL000")).thenReturn(Optional.empty());

        String result = fareService.update("FL000", new FareDTO("FL000", 0L, 0L));

        assertEquals("Fare not found with ID FL000", result);
    }

    @Test
    void testDeleteFareExists() {
        when(fareRepository.existsById("FL101")).thenReturn(true);

        String result = fareService.delete("FL101");

        assertEquals("Deleted Fare", result);
        verify(fareRepository).deleteById("FL101");
    }

    @Test
    void testDeleteFareNotExists() {
        when(fareRepository.existsById("FL202")).thenReturn(false);

        String result = fareService.delete("FL202");

        assertEquals("Fare Not Found", result);
        verify(fareRepository, never()).deleteById(anyString());
    }
}
