
package com.traveleasy.hotel_service.controller;

import com.traveleasy.hotel_service.entity.Booking;
import com.traveleasy.hotel_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create a booking
    @PostMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(
            @PathVariable Long roomId,
            @Valid @RequestBody Booking booking) {
        return bookingService.createBooking(roomId, booking);
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    // Get traveller booking history by email
    @GetMapping
    public List<Booking> getBookingHistory(
            @RequestParam String guestEmail) {
        return bookingService.getBookingHistory(guestEmail);
    }

    // Hotel partner: view reservations for a particular hotel
    @GetMapping("/hotel/{hotelId}")
    public List<Booking> getBookingsForHotel(
            @PathVariable Long hotelId) {
        return bookingService.getBookingsForHotel(hotelId);
    }

    // Cancel a booking
    @PatchMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}
