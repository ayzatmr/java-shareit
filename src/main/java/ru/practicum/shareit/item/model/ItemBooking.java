package ru.practicum.shareit.item.model;

import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public class ItemBooking {

    private Long id;

    private Long bookerId;

    private BookingStatus status;

    private LocalDateTime start;

    private LocalDateTime end;
}



