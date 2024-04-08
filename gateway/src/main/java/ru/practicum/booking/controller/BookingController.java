package ru.practicum.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.client.BookingClient;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.dto.NewBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.model.Constants.*;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId,
                             @Valid @RequestBody NewBookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@RequestHeader(USER_HEADER) Long userId,
                            @PathVariable Long bookingId,
                            @RequestParam Boolean approved) {
        return bookingClient.patch(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long bookingId) {
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                    @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                    @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return bookingClient.findAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                                @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return bookingClient.getAllOwnerBookings(userId, state, from, size);
    }
}