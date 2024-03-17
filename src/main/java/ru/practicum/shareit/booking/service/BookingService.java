package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    List<BookingDto> findAll(Long userId, BookingState state, int from, int size);

    BookingDto get(Long userId, Long bookingId);

    BookingDto create(Long userId, NewBookingDto bookingDto);

    BookingDto patch(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> findAllOwnerBookings(Long userId, BookingState state, int from, int size);
}