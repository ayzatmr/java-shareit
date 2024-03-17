package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getItems(long userId, int from, int size) {
        getUser(userId);
        Pageable pageRequest = PageRequest.of(from, size);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, pageRequest);
        return mergeBookingsAndComments(items);
    }

    @Override
    public ItemDto get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (item.getOwner().getId().equals(userId)) {
            List<ItemDto> itemDtos = mergeBookingsAndComments(List.of(item));
            return itemDtos.get(0);
        } else {
            ItemDto itemDto = itemMapper.toDto(item);
            itemDto.getComments().addAll(commentMapper.toDtoList(commentRepository.findAllByItemId(itemId)));
            return itemDto;
        }
    }

    @Override
    public ItemDto addNewItem(long userId, NewItemDto item) {
        User user = getUser(userId);
        Item itemModel = itemMapper.toModel(item);
        itemModel.setOwner(user);
        Item newItem = itemRepository.save(itemModel);
        return itemMapper.toDto(newItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        getUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("you can not delete that item");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto patchItem(long userId, NewItemDto itemDto, long itemId) {
        getUser(userId);
        Item itemFromDB = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (!itemFromDB.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("you can not update that item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemFromDB.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemFromDB.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemFromDB.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(itemFromDB);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageRequest = PageRequest.of(from, size);
        return itemRepository.findAllByNameOrDescription("%" + text.toLowerCase() + "%", pageRequest)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, NewCommentDto commentDto) {
        User user = getUser(userId);
        Item item = getItem(itemId);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBooker(itemId, userId);
        if (bookings.isEmpty()) {
            throw new ValidationException("You can not not leave comment on that item");
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User is not found"));
    }

    private Item getItem(final long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item is not found"));
    }

    private List<ItemDto> mergeBookingsAndComments(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds, BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            return itemMapper.toDtoList(items);
        }
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);
        Map<Long, List<Booking>> bookingsMap = bookings
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Comment>> commentsMap = comments
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toList()));
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toDto(item);
            List<Booking> itemBookings = bookingsMap.computeIfAbsent(item.getId(), k -> new ArrayList<>());
            List<Comment> itemComments = commentsMap.computeIfAbsent(item.getId(), k -> new ArrayList<>());
            itemDto.getComments().addAll(commentMapper.toDtoList(itemComments));

            Booking nextBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            Booking lastBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                            booking.getStatus().equals(BookingStatus.APPROVED))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            itemDto.setLastBooking(itemMapper.toItemBookingDto(lastBooking));
            itemDto.setNextBooking(itemMapper.toItemBookingDto(nextBooking));
            result.add(itemDto);
        }
        return result;
    }

}
