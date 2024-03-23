package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    public static final Sort BOOKINGS_SORTING = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(Long userId, NewBookingDto bookingDto) {
        User user = getUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("Item is not found"));
        isBookingValid(item, bookingDto);
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Item is already reserved");
        }
        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto patch(Long userId, Long bookingId, Boolean approved) {
        getUser(userId);
        Booking booking = getBooking(bookingId);
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Item is already reserved");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can not edit this booking");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findAll(Long userId, BookingState state, int from, int size) {
        getUser(userId);
        Pageable pageRequest = PageRequest.of(from, size, BOOKINGS_SORTING);
        List<Booking> bookings = getByBooker(state, userId, pageRequest);
        return bookings != null ? bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        getUser(userId);
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDto(booking);
        } else {
            throw new ObjectNotFoundException("Booking is not found");
        }
    }

    @Override
    public List<BookingDto> findAllOwnerBookings(Long userId, BookingState state, int from, int size) {
        getUser(userId);
        Pageable pageRequest = PageRequest.of(from, size, BOOKINGS_SORTING);
        List<Booking> bookings = getByUser(state, userId, pageRequest);
        return bookings != null ? bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private void isBookingValid(Item item, NewBookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Booking is not valid");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Booking is not valid");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User is not found"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.getByBookingId(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking is not found"));
    }

    private List<Booking> getByBooker(BookingState state, Long bookerId, Pageable pageRequest) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBooker(bookerId, pageRequest);
            case CURRENT:
                return bookingRepository.findCurrentByBooker(bookerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository.findPastByBooker(bookerId, LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository.findFutureByBooker(bookerId, LocalDateTime.now(), pageRequest);
            case WAITING:
                return bookingRepository.findByBookerAndStatus(bookerId, BookingStatus.WAITING, pageRequest);
            case REJECTED:
                return bookingRepository.findByBookerAndStatus(bookerId, BookingStatus.REJECTED, pageRequest);
        }
        return null;
    }

    private List<Booking> getByUser(BookingState state, Long userId, Pageable pageRequest) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItemOwnerId(userId, pageRequest);
            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
            case PAST:
                return bookingRepository.findPastByOwnerId(userId, LocalDateTime.now(), pageRequest);
            case FUTURE:
                return bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now(), pageRequest);
            case WAITING:
                return bookingRepository.finByOwnerAndStatus(userId, BookingStatus.WAITING, pageRequest);
            case REJECTED:
                return bookingRepository.finByOwnerAndStatus(userId, BookingStatus.REJECTED, pageRequest);
        }
        return null;
    }
}
