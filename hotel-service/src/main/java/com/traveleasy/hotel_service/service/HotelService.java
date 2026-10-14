package com.traveleasy.hotel_service.service;

import com.traveleasy.hotel_service.entity.Hotel;
import com.traveleasy.hotel_service.exception.ResourceNotFoundException;
import com.traveleasy.hotel_service.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Hotel createHotel(Hotel hotel) {
        validateHotelStatus(hotel.getStatus());
        return hotelRepository.save(hotel);
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public List<Hotel> searchHotels(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }

        return hotelRepository
                .findByLocationContainingIgnoreCaseAndStatusIgnoreCase(
                        location.trim(),
                        "ACTIVE"
                );
    }

    public Hotel updateHotel(Long id, Hotel updatedHotel) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found: " + id
                        )
                );

        hotel.setName(updatedHotel.getName());
        hotel.setLocation(updatedHotel.getLocation());
        hotel.setAddress(updatedHotel.getAddress());
        hotel.setDescription(updatedHotel.getDescription());
        hotel.setStarRating(updatedHotel.getStarRating());
        hotel.setAmenities(updatedHotel.getAmenities());
        hotel.setPolicies(updatedHotel.getPolicies());

        if (updatedHotel.getStatus() != null) {
            validateHotelStatus(updatedHotel.getStatus());
            hotel.setStatus(updatedHotel.getStatus().trim().toUpperCase());
        }

        return hotelRepository.save(hotel);
    }

    public Hotel updateHotelStatus(Long id, String status) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found: " + id
                        )
                );

        validateHotelStatus(status);
        hotel.setStatus(status.trim().toUpperCase());

        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found: " + id
                        )
                );

        hotelRepository.delete(hotel);
    }

    private void validateHotelStatus(String status) {
        if (status == null ||
                !List.of("ACTIVE", "INACTIVE", "MAINTENANCE")
                        .contains(status.trim().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Hotel status must be ACTIVE, INACTIVE or MAINTENANCE"
            );
        }
    }
}