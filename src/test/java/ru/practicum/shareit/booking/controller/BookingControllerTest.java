package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.model.Constants.USER_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final long userId = 1;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private NewBookingDto newBookingDto;

    private BookingDto bookingDto;


    @BeforeEach
    void beforeEach() {
        newBookingDto = NewBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
        bookingDto = new BookingDto();
    }


    @Test
    @SneakyThrows
    void addNewBooking() {
        when(bookingService.create(userId, newBookingDto))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(USER_HEADER, userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())));

        verify(bookingService, times(1)).create(userId, newBookingDto);
    }

    @Test
    @SneakyThrows
    void checkStartDayOfBooking() {
        newBookingDto.setStart(LocalDateTime.now().minusDays(2));
        mvc.perform(post("/bookings")
                        .header(USER_HEADER, userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(bookingService, never()).create(any(), any());
    }

    @Test
    @SneakyThrows
    void addNewBookingWithoutHeader() {

        mvc.perform(post("/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()));

        verify(bookingService, never()).create(any(), any());
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        Long bookingId = 2L;
        Boolean approved = true;
        when(bookingService.patch(userId, bookingId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())));

        verify(bookingService, times(1)).patch(userId, bookingId, approved);
    }

    @Test
    @SneakyThrows
    void getBookingById() {
        Long bookingId = 2L;
        when(bookingService.get(userId, bookingId))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())));

        verify(bookingService, times(1)).get(userId, bookingId);
    }

    @Test
    @SneakyThrows
    void getBookingByIdNotFound() {
        Long bookingId = 200L;
        when(bookingService.get(userId, bookingId))
                .thenThrow(ObjectNotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(ObjectNotFoundException.class, result.getResolvedException()));
    }

    @Test
    @SneakyThrows
    void findAlBookingsUnsupportedStatus() {
        int from = 0;
        int size = 50;
        mvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", "hell")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().is5xxServerError())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));
    }

    @Test
    @SneakyThrows
    void findAlBookings() {
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 50;
        when(bookingService.findAll(userId, state, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.[0].booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd())));

        verify(bookingService, times(1)).findAll(userId, state, from, size);
    }

    @Test
    @SneakyThrows
    void findAlBookingsCheckPaginationValidation() {
        BookingState state = BookingState.FUTURE;
        int from = -1;
        int size = 0;

        mvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state.name())
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }


    @Test
    @SneakyThrows
    void findAllOwnerBookings() {
        BookingState state = BookingState.FUTURE;
        int from = 1;
        int size = 5;
        when(bookingService.findAllOwnerBookings(userId, state, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.[0].booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd())));

        verify(bookingService, times(1)).findAllOwnerBookings(userId, state, from, size);
    }
}