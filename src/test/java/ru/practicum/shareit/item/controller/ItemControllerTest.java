package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.model.Constants.USER_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final long userId = 1;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();
    private final NewItemDto newItemDto = NewItemDto.builder()
            .id(1L)
            .name("name")
            .available(true)
            .description("description").build();
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void addNewItem() {
        when(itemService.addNewItem(userId, newItemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).addNewItem(userId, newItemDto);
    }

    @Test
    @SneakyThrows
    void patchItem() {
        when(itemService.patchItem(userId, newItemDto, newItemDto.getId()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", newItemDto.getId())
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).patchItem(userId, newItemDto, newItemDto.getId());
    }

    @Test
    @SneakyThrows
    void getItemById() {
        when(itemService.get(userId, itemDto.getId()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).get(userId, itemDto.getId());
    }

    @Test
    @SneakyThrows
    void deleteItemById() {
        doAnswer(invocation -> null).when(itemService).deleteItem(userId, itemDto.getId());
        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(userId, itemDto.getId());
    }

    @Test
    @SneakyThrows
    void getAllItemsByUserId() {
        when(itemService.getItems(userId, 0, 50))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemDto))))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).getItems(userId, 0, 50);
    }


    @Test
    @SneakyThrows
    void searchItems() {
        when(itemService.search(itemDto.getName(), 0, 50))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header(USER_HEADER, userId)
                        .param("text", newItemDto.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemDto))))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).search(itemDto.getName(), 0, 50);
    }

    @Test
    @SneakyThrows
    void addCommentToItem() {
        NewCommentDto addCommentDto = new NewCommentDto("comment");
        CommentDto commentDto = new CommentDto();
        when(itemService.addCommentToItem(userId, itemDto.getId(), addCommentDto))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(commentDto)))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));

        verify(itemService, times(1)).addCommentToItem(userId, itemDto.getId(), addCommentDto);
    }
}