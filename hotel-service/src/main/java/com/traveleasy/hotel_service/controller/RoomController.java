
package com.traveleasy.hotel_service.controller;

import com.traveleasy.hotel_service.entity.Room;
import com.traveleasy.hotel_service.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Create a room for a hotel
    @PostMapping("/hotels/{hotelId}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(
            @PathVariable Long hotelId,
            @Valid @RequestBody Room room) {
        return roomService.createRoom(hotelId, room);
    }

    // Get all rooms
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    // Get rooms belonging to a hotel
    @GetMapping("/hotels/{hotelId}/rooms")
    public List<Room> getRoomsByHotel(
            @PathVariable Long hotelId) {
        return roomService.getRoomsByHotel(hotelId);
    }

    // Get room by ID
    @GetMapping("/rooms/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    // Search rooms by hotel, dates and guest count
    @GetMapping("/hotels/{hotelId}/rooms/availability")
    public List<Room> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam(defaultValue = "1") int guests) {

        return roomService.getAvailableRooms(
                hotelId, checkIn, checkOut, guests);
    }

    // Update room details
    @PutMapping("/rooms/{id}")
    public Room updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    // Update room operational status
    @PatchMapping("/rooms/{id}/status")
    public Room updateRoomStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return roomService.updateRoomStatus(id, status);
    }

    // Delete a room
    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
