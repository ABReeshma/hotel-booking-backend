 package com.traveleasy.hotel_service.repository;

import com.traveleasy.hotel_service.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByLocationContainingIgnoreCaseAndStatusIgnoreCase(
            String location,
            String status
    );
}