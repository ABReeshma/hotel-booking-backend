
package com.traveleasy.hotel_service.service;

import com.traveleasy.hotel_service.entity.Booking;
import com.traveleasy.hotel_service.entity.Hotel;
import com.traveleasy.hotel_service.entity.Room;
import com.traveleasy.hotel_service.exception.BusinessConflictException;
import com.traveleasy.hotel_service.exception.ResourceNotFoundException;
import com.traveleasy.hotel_service.repository.BookingRepository;
import com.traveleasy.hotel_service.repository.HotelRepository;
import com.traveleasy.hotel_service.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public Booking createBooking(Long roomId, Booking request) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: " + roomId));

        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();

        if (checkIn == null || checkOut == null
                || !checkIn.isAfter(LocalDate.now().minusDays(1))
                || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Enter valid future check-in and check-out dates");
        }

        if (request.getNumberOfGuests() <= 0
                || request.getNumberOfGuests() > room.getCapacity()) {
            throw new IllegalArgumentException(
                    "Guest count must be within the room capacity");
        }

        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new BusinessConflictException(
                    "This room is not available for booking");
        }

        Hotel hotel = room.getHotel();

        if (hotel == null
                || !"ACTIVE".equalsIgnoreCase(hotel.getStatus())) {
            throw new BusinessConflictException(
                    "This hotel is not active for booking");
        }

        long overlappingBookings =
                bookingRepository.countOverlappingBookings(
                        roomId, checkIn, checkOut);

        if (overlappingBookings >= room.getTotalRooms()) {
            throw new BusinessConflictException(
                    "No rooms available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = room.getPricePerNight() * nights;

        request.setRoom(room);
        request.setTotalAmount(totalAmount);
        request.setStatus("CONFIRMED");

        return bookingRepository.save(request);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + id));
    }

    public List<Booking> getBookingHistory(String guestEmail) {
        if (guestEmail == null || guestEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Guest email is required");
        }

        return bookingRepository
                .findByGuestEmailIgnoreCase(guestEmail);
    }

    // Return reservations associated with a particular hotel
    public List<Booking> getBookingsForHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException(
                    "Hotel not found: " + hotelId);
        }

        return bookingRepository.findAll()
                .stream()
                .filter(booking ->
                        booking.getRoom() != null
                                && booking.getRoom().getHotel() != null
                                && booking.getRoom().getHotel()
                                .getId().equals(hotelId))
                .toList();
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessConflictException(
                    "Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");

        return bookingRepository.save(booking);
    }
}
