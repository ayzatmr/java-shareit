package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.model.Constants.USER_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final Long userId = 1L;
    @MockBean
    private ItemRequestService itemRequestService;
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
        when(itemRequestService.create(userId, newItemRequestDto))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1)
                        .content(objectMapper.writeValueAsString(newItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).create(userId, newItemRequestDto);
    }

    @Test
    @SneakyThrows
    public void getUserItemRequests() {
        when(itemRequestService.getUserItemRequests(userId))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header(USER_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1)).getUserItemRequests(userId);
    }


    @Test
    @SneakyThrows
    public void getAvailableItemRequests() {
        when(itemRequestService.getAvailableItemRequests(userId, 0, 50))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId)
                        .param("from", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1)).getAvailableItemRequests(userId, 0, 50);
    }

    @Test
    @SneakyThrows
    public void getItemRequestById() {
        long requestId = 2;
        when(itemRequestService.get(userId, requestId))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", String.valueOf(requestId))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).get(userId, requestId);
    }

    @Test
    @SneakyThrows
    public void getAvailableItemRequestsPaginationValidation() {
        when(itemRequestService.getAvailableItemRequests(userId, -1, -1))
                .thenThrow(ConstraintViolationException.class);
        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
        verify(itemRequestService, times(1)).getAvailableItemRequests(userId, -1, -1);
    }
}