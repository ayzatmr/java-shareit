package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapperImpl();

    private final User booker = User.builder()
            .id(1L)
            .name("vlad")
            .email("vlad@email.com")
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(5))
            .build();
    private final NewItemDto itemDto = NewItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .owner(booker)
            .build();

    @Test
    void toModel() {
        Item item = itemMapper.toModel(itemDto);
        assertThat(item.getId(), is(itemDto.getId()));
        assertThat(item.getDescription(), is(itemDto.getDescription()));
        assertThat(item.getAvailable(), is(itemDto.getAvailable()));
    }

    @Test
    void toDto() {
        ItemDto itemDto = itemMapper.toDto(item);
        assertThat(itemDto.getId(), is(item.getId()));
        assertThat(itemDto.getDescription(), is(item.getDescription()));
        assertThat(itemDto.getAvailable(), is(item.getAvailable()));
    }

    @Test
    void toDtoList() {
        List<ItemDto> itemDto = itemMapper.toDtoList(List.of(item));
        assertThat(itemDto.get(0).getId(), is(item.getId()));
        assertThat(itemDto.get(0).getDescription(), is(item.getDescription()));
        assertThat(itemDto.get(0).getAvailable(), is(item.getAvailable()));
    }

    @Test
    void toItemBookingDto() {
        ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(booking);
        assertThat(itemBookingDto.getId(), is(booking.getId()));
        assertThat(itemBookingDto.getStatus(), is(booking.getStatus()));
        assertThat(itemBookingDto.getBookerId(), is(booking.getBooker().getId()));
    }

    @Test
    void toNullDto() {
        ItemDto dto = itemMapper.toDto(null);
        assertThat(dto, nullValue());
    }

    @Test
    void toNullModel() {
        Item item = itemMapper.toModel(null);
        assertThat(item, nullValue());
    }

    @Test
    void toNullDtoList() {
        List<ItemDto> itemDtos = itemMapper.toDtoList(null);
        assertThat(itemDtos, nullValue());
    }
}