package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.common.model.Constants.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId,
                             @Valid @RequestBody NewBookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@RequestHeader(USER_HEADER) Long userId,
                            @PathVariable Long bookingId,
                            @RequestParam Boolean approved) {
        return bookingService.patch(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                    @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                    @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return bookingService.findAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                                @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return bookingService.findAllOwnerBookings(userId, state, from, size);
    }
}