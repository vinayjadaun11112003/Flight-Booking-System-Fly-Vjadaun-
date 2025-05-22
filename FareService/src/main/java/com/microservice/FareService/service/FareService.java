package com.microservice.FareService.service;


import com.microservice.FareService.dto.FareDTO;
import com.microservice.FareService.entity.FareEntity;
import com.microservice.FareService.repository.FareRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FareService {
    @Autowired
    private FareRepository fareRepository;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(FareService.class);



    // Method to save fare
    public FareDTO saveFare(FareDTO dto) {
        FareEntity fareEntity = new FareEntity(dto.getFlightId(), dto.getEconomyFare(), dto.getBusinessFare());
        FareEntity fareEntity1 = fareRepository.save(fareEntity);
        FareDTO faredto = new FareDTO(fareEntity1.getFlightId(), fareEntity1.getEconomyFare(), fareEntity1.getBusinessFare());
        logger.info("Fare saved with id : "+faredto.getFlightId());
        return faredto;
    }

    // Method to get fare with id
    public FareDTO getFare(String id){
        logger.info("Get Fare by id called");
       FareEntity fareEntity = fareRepository.findById(id).orElse(null);
       if(fareEntity==null){
           logger.info("fare not found with id : "+id);
           return null;
       }
       FareDTO dto = new FareDTO(fareEntity.getFlightId(),fareEntity.getEconomyFare(),fareEntity.getBusinessFare());
       logger.info("returned the fare with id : "+id);
       return dto;
    }

    // Method to update the fare with id
    public String update(String id, FareDTO updatedFare){
        logger.info("update fare called");
        return fareRepository.findById(id).map(fare -> {
            fare.setFlightId(updatedFare.getFlightId());
            fare.setEconomyFare(updatedFare.getEconomyFare());
            fare.setBusinessFare(updatedFare.getBusinessFare());
            fareRepository.save(fare);
            logger.info("Fare updated successfully for ID " + id);
            return "Fare updated successfully for ID " + id;
        }).orElse("Fare not found with ID " + id);
    }

    // Method to delete fare by id
    public String delete(String id){
        logger.info("delete fare by id called");
        if(fareRepository.existsById(id)) {
            fareRepository.deleteById(id);
            logger.info("fare deleted with id : "+ id);
            return "Deleted Fare";
        }else{
            logger.warn("fare not found with id : "+ id);
            return "Fare Not Found";
        }
    }
}
