package ru.practicum.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    private Long id;

    private ItemDto item;

    private UserDto booker;

    private BookingStatus status;

    private LocalDateTime start;

    private LocalDateTime end;
}
