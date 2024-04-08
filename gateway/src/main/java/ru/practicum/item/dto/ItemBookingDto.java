package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingStatus;

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

