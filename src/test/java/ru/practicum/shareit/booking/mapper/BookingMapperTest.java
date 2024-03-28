package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapBookingDto() {
        User booker = User.builder()
                .id(1L)
                .name("vlad")
                .email("vlad@email.com")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        BookingDto bookingDto = bookingMapper.toDto(booking);

        assertThat(bookingDto.getBooker().getId(), is(booker.getId()));
        assertThat(bookingDto.getStatus(), is(booking.getStatus()));
        assertThat(bookingDto.getStart(), is(booking.getStart()));
        assertThat(bookingDto.getEnd(), is(booking.getEnd()));
        assertThat(bookingDto.getId(), is(booking.getId()));
    }

    @Test
    void mapBookingDtoList() {
        User booker = User.builder()
                .id(1L)
                .name("vlad")
                .email("vlad@email.com")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        List<BookingDto> bookingDto = bookingMapper.toDtoList(List.of(booking));

        assertThat(bookingDto.get(0).getBooker().getId(), is(booker.getId()));
        assertThat(bookingDto.get(0).getStatus(), is(booking.getStatus()));
        assertThat(bookingDto.get(0).getStart(), is(booking.getStart()));
        assertThat(bookingDto.get(0).getEnd(), is(booking.getEnd()));
        assertThat(bookingDto.get(0).getId(), is(booking.getId()));
    }
}