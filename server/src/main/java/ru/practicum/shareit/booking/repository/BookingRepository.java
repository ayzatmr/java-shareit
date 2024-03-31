package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.id = ?1")
    Optional<Booking> getByBookingId(Long bookingId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.id = ?1 AND u.id = ?2 and b.status = 'APPROVED' and b.end <= CURRENT_TIMESTAMP")
    List<Booking> findAllByItemIdAndBooker(Long itemId, Long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.id IN ?1 and b.status = ?2")
    List<Booking> findAllByItemIdIn(List<Long> itemIds, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.item.owner.id = ?1")
    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?3")
    List<Booking> findCurrentByOwnerId(
            Long ownerId,
            LocalDateTime startBefore,
            LocalDateTime endAfter,
            Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = ?1 AND b.end <= ?2")
    List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime endBefore, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = ?1 AND b.start >= ?2")
    List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime startAfter, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = ?1 AND b.status = ?2")
    List<Booking> finByOwnerAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = ?1")
    List<Booking> findAllByBooker(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?3")
    List<Booking> findCurrentByBooker(
            Long ownerId,
            LocalDateTime startBefore,
            LocalDateTime endAfter,
            Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = ?1 AND b.end <= ?2")
    List<Booking> findPastByBooker(Long ownerId, LocalDateTime endBefore, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = ?1 AND b.start >= ?2")
    List<Booking> findFutureByBooker(Long ownerId, LocalDateTime startAfter, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = ?1 AND b.status = ?2")
    List<Booking> findByBookerAndStatus(Long ownerId, BookingStatus status, Pageable pageable);
}