package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@Positive @PathVariable Long userId) {
        return userService.get(userId);
    }

    @DeleteMapping()
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull @RequestHeader("X-Later-User-Id") long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        return userService.save(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto user,
                              @NotNull @RequestHeader("X-Later-User-Id") long userId) {
        return userService.update(user, userId);
    }
}