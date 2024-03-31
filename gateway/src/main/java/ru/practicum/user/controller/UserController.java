package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.Create;
import ru.practicum.user.dto.Update;
import ru.practicum.user.dto.UserDto;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public UserDto getUser(@Positive @PathVariable Long userId) {
        return userClient.get(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable long userId) {
        userClient.delete(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto user) {
        return userClient.save(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@Validated(Update.class) @RequestBody UserDto user,
                             @PathVariable long userId) {
        return userClient.patch(userId, user);
    }
}