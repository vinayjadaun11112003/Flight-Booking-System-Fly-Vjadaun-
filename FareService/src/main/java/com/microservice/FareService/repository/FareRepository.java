package com.microservice.FareService.repository;

import com.microservice.FareService.entity.FareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FareRepository extends JpaRepository<FareEntity,String> {
}
