package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.BOOKINGS_SORTING;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final Pageable pageRequest = PageRequest.of(0, 50, BOOKINGS_SORTING);
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Item item;
    private User itemOwner;
    private User user;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        itemOwner = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(itemOwner)
                .build();
        user = User.builder()
                .id(2L)
                .name("name")
                .email("email@email.com")
                .build();
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(user)
                .build();
    }

    @Test
    void createBooking() {
        NewBookingDto bookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        bookingService.create(user.getId(), bookingDto);

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void patchBooking() {
        when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(itemOwner));
        when(bookingRepository.getByBookingId(booking.getId()))
                .thenReturn(Optional.of(booking));

        bookingService.patch(itemOwner.getId(), booking.getId(), true);

        verify(userRepository, times(1)).findById(itemOwner.getId());
        verify(bookingRepository, times(1)).getByBookingId(booking.getId());
    }

    @Test
    void getByBookingId() {
        when(userRepository.findById(itemOwner.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.getByBookingId(booking.getId()))
                .thenReturn(Optional.of(booking));

        bookingService.get(itemOwner.getId(), booking.getId());

        verify(userRepository, times(1)).findById(itemOwner.getId());
        verify(bookingRepository, times(1)).getByBookingId(booking.getId());
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    void findAllOwnerBookings() {
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 50;
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerId(user.getId(), pageRequest))
                .thenReturn(List.of(booking));

        bookingService.findAllOwnerBookings(user.getId(), state, from, size);

        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerId(user.getId(), pageRequest);
    }

    @Test
    void findAllBookings() {
        BookingState state = BookingState.ALL;
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(user.getId(), pageRequest))
                .thenReturn(List.of(booking));

        bookingService.findAll(user.getId(), state, 0, 50);

        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findAllByBooker(user.getId(), pageRequest);
    }
}