package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.BOOKINGS_SORTING;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class BookingRepositoryTest {

    private static final Pageable pageRequest = PageRequest.of(0, 50, BOOKINGS_SORTING);
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private User owner;
    private User booker;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking currentBooking;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(
                User.builder()
                        .name("owner")
                        .email("albert@email.com")
                        .build());
        booker = userRepository.save(
                User.builder()
                        .name("booker")
                        .email("sivester@email.com")
                        .build());
        item = itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("description")
                        .available(true)
                        .owner(owner)
                        .build());
        pastBooking = bookingRepository.save(
                Booking.builder()
                        .status(BookingStatus.APPROVED)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusDays(4))
                        .item(item)
                        .booker(booker)
                        .build());
        currentBooking = bookingRepository.save(
                Booking.builder()
                        .status(BookingStatus.WAITING)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().plusDays(4))
                        .item(item)
                        .booker(booker)
                        .build());
        futureBooking = bookingRepository.save(
                Booking.builder()
                        .status(BookingStatus.WAITING)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(4))
                        .item(item)
                        .booker(booker)
                        .build());
    }

    @AfterAll
    public void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void getByBookingId() {
        Booking newBooking = bookingRepository.getByBookingId(pastBooking.getId()).get();
        assertThat(newBooking.getId(), is(pastBooking.getId()));
        assertThat(newBooking.getStatus(), is(pastBooking.getStatus()));
    }

    @Test
    void getByBookingIdNotFound() {
        Optional<Booking> booking = bookingRepository.getByBookingId(1000L);
        assertTrue(booking.isEmpty());
    }

    @Test
    void findAllByItemIdAndBooker() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBooker(item.getId(), booker.getId());
        assertThat(bookings, is(List.of(pastBooking)));
    }

    @Test
    void findAllByItemIdIn() {
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(List.of(item.getId()), BookingStatus.WAITING);
        assertThat(bookings, is(List.of(currentBooking, futureBooking)));
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(owner.getId(), pageRequest);
        assertThat(bookings, is(List.of(futureBooking, currentBooking, pastBooking)));
    }


    @Test
    void findCurrentByOwnerId() {
        List<Booking> bookings = bookingRepository.findCurrentByOwnerId(owner.getId(), now(), now(), pageRequest);
        assertThat(bookings, is(List.of(currentBooking)));
    }

    @Test
    void findPastByOwnerId() {
        List<Booking> bookings = bookingRepository.findPastByOwnerId(owner.getId(), now(), pageRequest);
        assertThat(bookings, is(List.of(pastBooking)));
    }

    @Test
    void findFutureByOwnerId() {
        List<Booking> bookings = bookingRepository.findFutureByOwnerId(owner.getId(), now(), pageRequest);
        assertThat(bookings, is(List.of(futureBooking)));
    }

    @Test
    void finByOwnerAndStatus() {
        List<Booking> bookings = bookingRepository.finByOwnerAndStatus(owner.getId(), BookingStatus.WAITING, pageRequest);
        assertEquals(bookings, List.of(futureBooking, currentBooking));
    }

    @Test
    void findAllByBooker() {
        List<Booking> bookings = bookingRepository.findAllByBooker(booker.getId(), pageRequest);
        assertEquals(bookings, List.of(futureBooking, currentBooking, pastBooking));
    }

    @Test
    void findCurrentByBooker() {
        List<Booking> bookings = bookingRepository.findCurrentByBooker(booker.getId(), now(), now(), pageRequest);
        assertEquals(bookings, List.of(currentBooking));
    }

    @Test
    void findPastByBooker() {
        List<Booking> bookings = bookingRepository.findPastByBooker(booker.getId(), now(), pageRequest);
        assertThat(bookings, is(List.of(pastBooking)));
    }

    @Test
    void findFutureByBooker() {
        List<Booking> bookings = bookingRepository.findFutureByBooker(booker.getId(), now(), pageRequest);
        assertThat(bookings, is(List.of(futureBooking)));
    }

    @Test
    void findByBookerAndStatus() {
        List<Booking> bookings = bookingRepository.findByBookerAndStatus(booker.getId(), BookingStatus.WAITING, pageRequest);
        assertThat(bookings, is(List.of(futureBooking, currentBooking)));
    }
}