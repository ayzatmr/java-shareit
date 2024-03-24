package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@email.com")
            .build();
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .build();
    }

    @Test
    void addUser() {
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.toModel(userDto))
                .thenReturn(user);

        userService.save(userDto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        userService.get(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findUserByIdNotFound() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.get(user.getId()));
        assertThat(e.getMessage(), is("user is not found"));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        userService.getAllUsers();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserById() {
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}