package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User owner;
    private User user;
    private NewItemDto newItemDto;
    private ItemDto itemDto;
    private Item item;
    private Booking booking;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@email.com")
                .build();
        user = User.builder()
                .id(2L)
                .name("user")
                .email("user@email.com")
                .build();
        newItemDto = NewItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addNewItem() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.toModel(newItemDto))
                .thenReturn(item);
        when(itemRequestRepository.findById(newItemDto.getRequestId()))
                .thenReturn(Optional.of(new ItemRequest()));

        itemService.addNewItem(owner.getId(), newItemDto);

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).save(item);
        verify(itemRequestRepository, times(1)).findById(newItemDto.getRequestId());
    }

    @Test
    void updateItem() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        itemService.patchItem(owner.getId(), newItemDto, newItemDto.getId());

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdIn(List.of(item.getId()), BookingStatus.APPROVED))
                .thenReturn(List.of(booking, booking2));
        when(commentMapper.toDtoList(any()))
                .thenReturn(List.of(new CommentDto()));
        when(itemMapper.toDto(any()))
                .thenReturn(itemDto);

        itemService.get(item.getId(), owner.getId());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).findAllByItemIdIn(List.of(item.getId()), BookingStatus.APPROVED);
    }


    @Test
    void findAllItemsByUserId() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(List.of(item.getId()), BookingStatus.APPROVED))
                .thenReturn(List.of(booking, booking2));
        when(commentMapper.toDtoList(any()))
                .thenReturn(List.of(new CommentDto()));
        when(itemMapper.toDto(any()))
                .thenReturn(itemDto);

        List<ItemDto> items = itemService.getItems(user.getId(), 0, 50);

        assertThat(items.size(), is(1));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findAllByOwnerIdOrderById(anyLong(), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemIdIn(List.of(item.getId()), BookingStatus.APPROVED);
    }

    @Test
    void findAllByNameOrDescription() {
        when(itemRepository.findAllByNameOrDescription(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));
        itemService.search(item.getName(), 0, 50);
        verify(itemRepository, times(1)).findAllByNameOrDescription(anyString(), any(Pageable.class));
    }

    @Test
    void addCommentToItem() {
        NewCommentDto addCommentDto = new NewCommentDto("comment");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBooker(item.getId(), user.getId()))
                .thenReturn(List.of(booking, booking2));
        when(commentRepository.save(any()))
                .thenReturn(new Comment());

        itemService.addCommentToItem(user.getId(), item.getId(), addCommentDto);

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).findAllByItemIdAndBooker(item.getId(), user.getId());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addRequestToItemNotFound() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .user(user)
                .build();
        Item newItem = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .build();
        when(itemRequestRepository.findById(1L))
                .thenThrow(ObjectNotFoundException.class);
        assertThrows(ObjectNotFoundException.class,
                () -> ReflectionTestUtils.invokeMethod(itemService, "addRequestToItem", newItemDto, newItem));
        verify(itemRequestRepository, times(1)).findById(1L);

    }

    @Test
    void addRequestToItem() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .user(user)
                .build();
        Item newItem = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .build();
        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        ReflectionTestUtils.invokeMethod(itemService, "addRequestToItem", newItemDto, newItem);
        verify(itemRequestRepository, times(1)).findById(1L);
    }
}