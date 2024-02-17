package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto save(UserDto user);

    UserDto patch(UserPatchDto user, Long userId);

    void delete(long userId);

    UserDto get(Long userId);
}