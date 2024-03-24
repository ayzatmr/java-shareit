package ru.practicum.shareit.item.integration;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingService bookingService;

    private User user;

    private User user2;

    private NewItemDto itemDto;

    @BeforeAll
    void beforeAll() {
        user = userRepository.save(
                User.builder()
                        .name("Rick")
                        .email("rick@mail.com")
                        .build());
        user2 = userRepository.save(
                User.builder()
                        .name("Morti")
                        .email("morti@mail.com")
                        .build());
        itemDto = NewItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    @AfterAll
    public void afterAll() {
        userRepository.deleteAll();
    }

    @Test
    void addNewItem() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), is(itemDto.getName()));
        assertThat(savedItem.getDescription(), is(itemDto.getDescription()));
        assertThat(savedItem.getAvailable(), is(itemDto.getAvailable()));
    }

    @Test
    void deleteItem() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        itemService.deleteItem(user.getId(), savedItem.getId());
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.get(savedItem.getId(), user.getId()));
        assertThat(e.getMessage(), is("item is not found"));
    }

    @Test
    void deleteUserNotFound() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.deleteItem(200L, user2.getId()));
        assertThat(e.getMessage(), is("User is not found"));
    }

    @Test
    void getItemByIdNotFound() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.get(200L, user2.getId()));
        assertThat(e.getMessage(), is("item is not found"));
    }

    @Test
    void patchItem() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);

        NewItemDto itemUpdateDto = NewItemDto.builder()
                .name("new name")
                .description("new description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.patchItem(user.getId(), itemUpdateDto, savedItem.getId());
        assertThat(updatedItem.getName(), is(itemUpdateDto.getName()));
        assertThat(updatedItem.getDescription(), is(itemUpdateDto.getDescription()));
        assertThat(updatedItem.getAvailable(), is(itemUpdateDto.getAvailable()));
    }

    @Test
    void getItemByOwner() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        ItemDto item = itemService.get(savedItem.getId(), user.getId());

        assertThat(item, notNullValue());
        assertThat(item.getComments(), emptyIterable());
        assertNull(item.getLastBooking());
        assertNull(item.getNextBooking());
    }

    @Test
    void getItemNotByOwner() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        ItemDto item = itemService.get(savedItem.getId(), user2.getId());

        assertThat(item.getComments(), emptyIterable());
        assertNull(item.getLastBooking());
        assertNull(item.getNextBooking());
    }

    @Test
    void getItemsWithBookings() {

        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        long itemId = savedItem.getId();
        NewBookingDto addBookingDto1 = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        BookingDto bookingDto = bookingService.create(user2.getId(), addBookingDto1);
        bookingService.patch(user.getId(), bookingDto.getId(), true);

        List<ItemDto> items = itemService.getItems(user.getId(), 0, 50);

        assertThat(items.size(), is(1));
        assertThat(items.get(0).getNextBooking().getStart(), is(bookingDto.getStart()));
        assertThat(items.get(0).getNextBooking().getEnd(), is(bookingDto.getEnd()));
        assertThat(items.get(0).getNextBooking().getBookerId(), is(user2.getId()));
    }

    @Test
    void searchItems() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);

        List<ItemDto> items = itemService.search(savedItem.getName(), 0, 50);

        assertThat(items.size(), is(1));
        assertThat(items.get(0).getName(), is(itemDto.getName()));
        assertThat(items.get(0).getDescription(), is(itemDto.getDescription()));
    }

    @Test
    void searchItemsNotFound() {
        List<ItemDto> items = itemService.search("", 0, 50);
        assertThat(items.size(), is(0));
    }

    @Test
    void addCommentToItem() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        NewBookingDto addBookingDto1 = NewBookingDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        BookingDto bookingDto = bookingService.create(user2.getId(), addBookingDto1);
        bookingService.patch(user.getId(), bookingDto.getId(), true);

        NewCommentDto addCommentDto = new NewCommentDto("new comment");
        CommentDto commentDto = itemService.addCommentToItem(user2.getId(), savedItem.getId(), addCommentDto);

        assertThat(commentDto, notNullValue());
        assertThat(commentDto.getAuthorName(), is(user2.getName()));
        assertThat(commentDto.getText(), is(addCommentDto.getText()));
        assertThat(commentDto.getCreated(), lessThan(LocalDateTime.now()));
    }

    @Test
    void addCommentToItemWithoutBooking() {
        ItemDto savedItem = itemService.addNewItem(user.getId(), itemDto);
        NewCommentDto addCommentDto = new NewCommentDto("new comment");
        ValidationException e = assertThrows(ValidationException.class,
                () -> itemService.addCommentToItem(user2.getId(), savedItem.getId(), addCommentDto));
        assertThat(e.getMessage(), is("You can not not leave comment on that item"));

    }
}