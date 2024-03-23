package ru.practicum.shareit.booking.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;

    private User booker;

    private Item item;

    private NewBookingDto currentBookingDto;

    private NewBookingDto futureBookingDto;

    private NewBookingDto pastBookingDto;

    @BeforeAll
    void beforeAll() {
        owner = userRepository.save(User.builder()
                .name("user")
                .email("van@mail.ru")
                .build());

        booker = userRepository.save(User.builder()
                .name("booker")
                .email("alise@mail.ru")
                .build());

        item = itemRepository.save(Item.builder()
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build());
        currentBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        futureBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        pastBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .build();
    }

    @AfterAll
    public void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void addBooking() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        assertThat(addedBooking.getId(), notNullValue());
        assertThat(addedBooking.getItem().getId(), is(item.getId()));
        assertThat(addedBooking.getStart(), notNullValue());
        assertThat(addedBooking.getEnd(), notNullValue());
        assertThat(addedBooking.getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void addBookingByNotExistingUser() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(100L, currentBookingDto));
        assertThat(e.getMessage(), is("User is not found"));
    }

    @Test
    void changeBookingStatus() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        BookingDto bookingDto = bookingService.patch(owner.getId(), addedBooking.getId(), true);
        assertThat(bookingDto.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void changeBookingStatusNotByOwner() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.patch(booker.getId(), addedBooking.getId(), true));
        assertThat(e.getMessage(), is("You can not edit this booking"));
    }

    @Test
    void changeBookingStatusOfAlreadyReservedItem() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        bookingService.patch(owner.getId(), addedBooking.getId(), true);
        ValidationException e = assertThrows(ValidationException.class,
                () -> bookingService.patch(owner.getId(), addedBooking.getId(), true));
        assertThat(e.getMessage(), is("Item is already reserved"));
    }

    @Test
    void getBookingById() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        BookingDto booking = bookingService.get(owner.getId(), addedBooking.getId());
        assertThat(booking.getStatus(), is(BookingStatus.WAITING));
        assertThat(booking.getBooker().getId(), is(booker.getId()));
        assertThat(booking.getItem().getId(), is(item.getId()));
    }

    @Test
    void getBookingNotByOwner() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        User user = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();
        User newUser = userRepository.save(user);
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.get(newUser.getId(), addedBooking.getId()));
        assertThat(e.getMessage(), is("Booking is not found"));
    }

    @Test
    void findAllBookingsByOwner() {
        BookingDto addedBooking = bookingService.create(booker.getId(), futureBookingDto);
        List<BookingDto> bookings = bookingService.findAllOwnerBookings(owner.getId(), BookingState.ALL, 0, 50);
        assertThat(bookings, is(List.of(addedBooking)));
    }

    @Test
    void findAllPastBookingsByBooker() {
        BookingDto addedBooking3 = bookingService.create(booker.getId(), pastBookingDto);
        List<BookingDto> bookings = bookingService.findAll(booker.getId(), BookingState.PAST, 0, 50);
        assertThat(bookings, notNullValue());
        assertThat(bookings, is(List.of(addedBooking3)));
    }

    @Test
    void findAllFutureBookingsByBooker() {
        BookingDto addedBooking2 = bookingService.create(booker.getId(), futureBookingDto);
        List<BookingDto> bookings = bookingService.findAll(booker.getId(), BookingState.FUTURE, 0, 50);
        assertThat(bookings, is(List.of(addedBooking2)));
    }

    @Test
    void findAllWaitingBookingsByBooker() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        List<BookingDto> bookings = bookingService.findAll(booker.getId(), BookingState.WAITING, 0, 50);
        assertThat(bookings, is(List.of(addedBooking)));
    }

    @Test
    void findAllRejectedBookingsByBooker() {
        BookingDto addedBooking = bookingService.create(booker.getId(), currentBookingDto);
        BookingDto patched = bookingService.patch(owner.getId(), addedBooking.getId(), false);
        List<BookingDto> bookings = bookingService.findAll(booker.getId(), BookingState.REJECTED, 0, 50);
        assertThat(bookings, is(List.of(patched)));
    }

}