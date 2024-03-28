package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
}