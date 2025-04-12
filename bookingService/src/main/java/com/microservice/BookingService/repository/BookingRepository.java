package com.microservice.BookingService.repository;

import com.microservice.BookingService.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.awt.print.Book;
import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,String> {
    List<BookingEntity> findByUserId(String id);



}
