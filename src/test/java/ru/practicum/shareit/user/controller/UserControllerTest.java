package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.common.exception.AlreadyExistException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("test@mail.com")
                .build();
    }

    @Test
    @SneakyThrows
    void addUser() {
        when(userService.save(userDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).save(userDto);
    }

    @Test
    @SneakyThrows
    void addUserNotValidUser() {
        userDto.setEmail(null);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
        verify(userService, never()).save(userDto);
    }

    @Test
    @SneakyThrows
    void checkHttpRequestMethodNotSupportedException() {
        mvc.perform(post("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result ->
                        assertInstanceOf(HttpRequestMethodNotSupportedException.class, result.getResolvedException()));
        verify(userService, never()).save(userDto);
    }

    @Test
    @SneakyThrows
    void updateUser() {
        when(userService.patch(userDto, userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).patch(userDto, userDto.getId());
    }

    @Test
    @SneakyThrows
    void getAlreadyExistExceptionOnUserUpdate() {
        Long userId = 300L;
        when(userService.patch(userDto, userId))
                .thenThrow(AlreadyExistException.class);

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result ->
                        assertInstanceOf(AlreadyExistException.class, result.getResolvedException()));

        verify(userService, times(1)).patch(userDto, userId);
    }

    @Test
    @SneakyThrows
    void getUserById() {
        when(userService.get(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).get(userDto.getId());
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(userDto))))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.length()", is(1)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void deleteUserById() {
        mvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().is2xxSuccessful());

        verify(userService, times(1)).delete(userDto.getId());
    }
}