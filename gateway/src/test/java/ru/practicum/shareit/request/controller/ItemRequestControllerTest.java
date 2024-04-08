package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.request.client.ItemRequestClient;
import ru.practicum.request.controller.ItemRequestController;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.NewItemRequestDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.common.model.Constants.USER_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final Long userId = 1L;
    @MockBean
    private ItemRequestClient itemRequestClient;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private NewItemRequestDto newItemRequestDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        newItemRequestDto = new NewItemRequestDto("description");
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @SneakyThrows
    public void addNewItemRequest() {
        when(itemRequestClient.create(userId, newItemRequestDto))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1)
                        .content(objectMapper.writeValueAsString(newItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestClient, times(1)).create(userId, newItemRequestDto);
    }

    @Test
    @SneakyThrows
    public void addNewItemRequestNotValidRequestBody() {
        NewItemRequestDto requestDto = new NewItemRequestDto();

        mvc.perform(post("/requests")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(itemRequestClient, never()).create(eq(userId), any());
    }

    @Test
    @SneakyThrows
    public void getUserItemRequests() {
        when(itemRequestClient.getUserItemRequests(userId))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header(USER_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestClient, times(1)).getUserItemRequests(userId);
    }


    @Test
    @SneakyThrows
    public void getAvailableItemRequests() {
        when(itemRequestClient.getAvailableItemRequests(userId, 0, 50))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId)
                        .param("from", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestClient, times(1)).getAvailableItemRequests(userId, 0, 50);
    }

    @Test
    @SneakyThrows
    public void getAvailableItemRequestsPaginationValidation() {
        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @SneakyThrows
    public void getItemRequestById() {
        long requestId = 2;
        when(itemRequestClient.get(userId, requestId))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", String.valueOf(requestId))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestClient, times(1)).get(userId, requestId);
    }
}