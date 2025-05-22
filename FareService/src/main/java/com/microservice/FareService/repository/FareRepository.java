package com.microservice.FareService.repository;

import com.microservice.FareService.entity.FareEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface FareRepository extends MongoRepository<FareEntity,String> {
}
