package com.microservice.FareService.service;


import com.microservice.FareService.dto.FareDTO;
import com.microservice.FareService.entity.FareEntity;
import com.microservice.FareService.repository.FareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FareService {
    @Autowired
    private FareRepository fareRepository;

    public FareDTO saveFare(FareDTO dto) {
        FareEntity fareEntity = new FareEntity(dto.getFlightId(), dto.getEconomyFare(), dto.getBusinessFare());
        FareEntity fareEntity1 = fareRepository.save(fareEntity);
        FareDTO faredto = new FareDTO(fareEntity1.getFlightId(), fareEntity1.getEconomyFare(), fareEntity1.getBusinessFare());
        return faredto;
    }
    public FareDTO getFare(String id){
       FareEntity fareEntity = fareRepository.findById(id).orElse(null);
       if(fareEntity==null){
           return null;
       }
       FareDTO dto = new FareDTO(fareEntity.getFlightId(),fareEntity.getEconomyFare(),fareEntity.getBusinessFare());
       return dto;
    }

    public String update(String id, FareDTO updatedFare){
        return fareRepository.findById(id).map(fare -> {
            fare.setFlightId(updatedFare.getFlightId());
            fare.setEconomyFare(updatedFare.getEconomyFare());
            fare.setBusinessFare(updatedFare.getBusinessFare());
            fareRepository.save(fare);
            return "Fare updated successfully for ID " + id;
        }).orElse("Fare not found with ID " + id);
    }

    public String delete(String id){
        if(fareRepository.existsById(id)) {
            fareRepository.deleteById(id);
            return "Deleted Fare";
        }else{
            return "Fare Not Found";
        }
    }
}
