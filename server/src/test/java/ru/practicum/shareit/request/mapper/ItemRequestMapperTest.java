package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    private final NewItemRequestDto newItemRequestDto = NewItemRequestDto.builder()
            .description("description")
            .build();

    @Test
    public void toModel() {
        ItemRequest itemRequest = itemRequestMapper.toModel(newItemRequestDto);
        assertThat(itemRequest.getDescription(), is(newItemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), is(notNullValue()));
    }

    @Test
    void toNullDto() {
        ItemRequestDto dto = itemRequestMapper.toDto(null);
        assertThat(dto, nullValue());
    }

    @Test
    void toNullModel() {
        ItemRequest item = itemRequestMapper.toModel(null);
        assertThat(item, nullValue());
    }

    @Test
    void toNullDtoList() {
        List<ItemRequestDto> itemDtos = itemRequestMapper.toDtoList(null);
        assertThat(itemDtos, nullValue());
    }
}