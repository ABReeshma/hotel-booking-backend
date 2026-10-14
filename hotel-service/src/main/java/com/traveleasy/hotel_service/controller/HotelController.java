package com.traveleasy.hotel_service.controller;

import com.traveleasy.hotel_service.entity.Hotel;
import com.traveleasy.hotel_service.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel createHotel(@Valid @RequestBody Hotel hotel) {
        return hotelService.createHotel(hotel);
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id)
                .orElseThrow(() ->
                        new com.traveleasy.hotel_service.exception
                                .ResourceNotFoundException(
                                "Hotel not found: " + id
                        )
                );
    }

    @GetMapping("/search")
    public List<Hotel> searchHotels(
            @RequestParam String location) {
        return hotelService.searchHotels(location);
    }

    @PutMapping("/{id}")
    public Hotel updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody Hotel hotel) {
        return hotelService.updateHotel(id, hotel);
    }

    @PatchMapping("/{id}/status")
    public Hotel updateHotelStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return hotelService.updateHotelStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
    }
}