package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.item.client.ItemClient;
import ru.practicum.item.controller.ItemController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.NewCommentDto;
import ru.practicum.item.dto.NewItemDto;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.common.model.Constants.USER_HEADER;


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
    private ItemClient itemClient;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void addNewItem() {
        when(itemClient.add(userId, newItemDto))
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

        verify(itemClient, times(1)).add(userId, newItemDto);
    }

    @Test
    @SneakyThrows
    void patchItem() {
        when(itemClient.patch(userId, newItemDto, newItemDto.getId()))
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

        verify(itemClient, times(1)).patch(userId, newItemDto, newItemDto.getId());
    }

    @Test
    @SneakyThrows
    void patchItemNotValidBody() {
        NewItemDto notValid = NewItemDto.builder()
                .id(2L)
                .name("QAZWSXQAZWSXSS".repeat(15))
                .build();
        mvc.perform(patch("/items/{itemId}", notValid.getId())
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValid)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    @SneakyThrows
    void getItemById() {
        when(itemClient.get(userId, itemDto.getId()))
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

        verify(itemClient, times(1)).get(userId, itemDto.getId());
    }

    @Test
    @SneakyThrows
    void deleteItemById() {
        doAnswer(invocation -> null).when(itemClient).delete(userId, itemDto.getId());
        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).delete(userId, itemDto.getId());
    }

    @Test
    @SneakyThrows
    void getAllItemsByUserId() {
        when(itemClient.findAll(userId, 0, 50))
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

        verify(itemClient, times(1)).findAll(userId, 0, 50);
    }

    @Test
    @SneakyThrows
    void getAllItemsByUserIdPaginationValidation() {
        mvc.perform(get("/items")
                        .header(USER_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }


    @Test
    @SneakyThrows
    void searchItems() {
        when(itemClient.search(userId, itemDto.getName(), 0, 50))
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

        verify(itemClient, times(1)).search(userId, itemDto.getName(), 0, 50);
    }

    @Test
    @SneakyThrows
    void addCommentToItem() {
        NewCommentDto addCommentDto = new NewCommentDto("comment");
        CommentDto commentDto = new CommentDto();
        when(itemClient.addCommentToItem(userId, itemDto.getId(), addCommentDto))
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

        verify(itemClient, times(1)).addCommentToItem(userId, itemDto.getId(), addCommentDto);
    }

    @Test
    @SneakyThrows
    void addCommentToItemNotValidData() {
        NewCommentDto addCommentDto = new NewCommentDto("");
        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }
}