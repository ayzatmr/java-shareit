package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final BookingMapper bookingMapper;

    @Override
    public List<ItemDto> getItems(long userId) {
        getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
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
            itemDto.setComments(commentRepository.findAllByItemId(itemId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList()));
            return itemDto;
        }
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto item) {
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
    public ItemDto patchItem(long userId, ItemDto itemDto, long itemId) {
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
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameOrDescription("%" + text.toLowerCase() + "%")
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        User user = getUser(userId);
        Item item = getItem(itemId);
        boolean isAvailable = bookingRepository.findAllByItemIdAndBooker(itemId, userId)
                .stream()
                .anyMatch(booking -> booking.getBooker().getId().equals(userId)
                        && booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED));
        if (!isAvailable) {
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
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);
        if (bookings.isEmpty()) {
            return items.stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
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
            itemDto.setComments(commentMapper.toDtoList(itemComments));

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
