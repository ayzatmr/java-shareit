package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {

    private Long id;

    private Long bookerId;

    private BookingStatus status;

    private LocalDateTime start;

    private LocalDateTime end;
}

