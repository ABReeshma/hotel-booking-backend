
package com.traveleasy.hotel_service.service;

import com.traveleasy.hotel_service.entity.Hotel;
import com.traveleasy.hotel_service.entity.Room;
import com.traveleasy.hotel_service.entity.Booking;
import com.traveleasy.hotel_service.exception.ResourceNotFoundException;
import com.traveleasy.hotel_service.repository.HotelRepository;
import com.traveleasy.hotel_service.repository.RoomRepository;
import com.traveleasy.hotel_service.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public RoomService(
            RoomRepository roomRepository,
            HotelRepository hotelRepository,
            BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
    }

    public Room createRoom(Long hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found: " + hotelId));

        if (!"ACTIVE".equalsIgnoreCase(hotel.getStatus())) {
            throw new IllegalArgumentException(
                    "Rooms can only be added to an active hotel");
        }

        validateRoomStatus(room.getStatus());
        room.setHotel(hotel);

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException(
                    "Hotel not found: " + hotelId);
        }

        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: " + id));
    }

    public List<Room> getAvailableRooms(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            int guests) {

        if (checkIn == null || checkOut == null
                || !checkIn.isAfter(LocalDate.now().minusDays(1))
                || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Enter valid future check-in and check-out dates");
        }

        if (guests <= 0) {
            throw new IllegalArgumentException(
                    "Guest count must be positive");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found: " + hotelId));

        if (!"ACTIVE".equalsIgnoreCase(hotel.getStatus())) {
            return List.of();
        }

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())
                    || room.getCapacity() < guests) {
                continue;
            }

            long overlappingBookings =
                    bookingRepository.countOverlappingBookings(
                            room.getId(), checkIn, checkOut);

            if (overlappingBookings < room.getTotalRooms()) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        Room room = getRoomById(id);

        room.setRoomType(updatedRoom.getRoomType());
        room.setPricePerNight(updatedRoom.getPricePerNight());
        room.setCapacity(updatedRoom.getCapacity());
        room.setTotalRooms(updatedRoom.getTotalRooms());
        room.setAmenities(updatedRoom.getAmenities());

        if (updatedRoom.getStatus() != null) {
            validateRoomStatus(updatedRoom.getStatus());
            room.setStatus(updatedRoom.getStatus().toUpperCase());
        }

        return roomRepository.save(room);
    }

    public Room updateRoomStatus(Long id, String status) {
        Room room = getRoomById(id);

        validateRoomStatus(status);
        room.setStatus(status.toUpperCase());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);

        boolean hasBookings = bookingRepository.findAll()
                .stream()
                .anyMatch(booking ->
                        booking.getRoom().getId().equals(id));

        if (hasBookings) {
            throw new IllegalArgumentException(
                    "Room has booking history; mark it UNAVAILABLE instead");
        }

        roomRepository.delete(room);
    }

    private void validateRoomStatus(String status) {
        if (status == null ||
                !List.of("AVAILABLE", "UNAVAILABLE", "MAINTENANCE")
                        .contains(status.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Room status must be AVAILABLE, UNAVAILABLE or MAINTENANCE");
        }
    }
}
