 package com.traveleasy.hotel_service.repository;

import com.traveleasy.hotel_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestEmailIgnoreCase(String guestEmail);

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status <> 'CANCELLED'
          AND b.checkInDate < :checkOut
          AND b.checkOutDate > :checkIn
        """)
    long countOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
