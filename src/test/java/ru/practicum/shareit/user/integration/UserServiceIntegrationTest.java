package ru.practicum.shareit.user.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.common.exception.AlreadyExistException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("name@email.com")
            .build();
    @Autowired
    private UserService userService;

    @Test
    void addUser() {
        UserDto savedUser = userService.save(userDto);

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), is(userDto.getName()));
        assertThat(savedUser.getEmail(), is(userDto.getEmail()));
    }

    @Test
    void updateUserWithSameEmail() {
        UserDto user = userService.save(userDto);
        UserDto newUserdto = UserDto.builder()
                .name("hey")
                .email("make@email.com")
                .build();
        userService.save(newUserdto);
        AlreadyExistException e = assertThrows(AlreadyExistException.class,
                () -> userService.patch(newUserdto, user.getId()));
        assertThat(e.getMessage(), is("email should be unique"));
    }

    @Test
    void updateUserNotFound() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.patch(userDto, 100L));
        assertThat(e.getMessage(), is("user is not found"));
    }

    @Test
    void updateUser() {
        UserDto savedUser = userService.save(userDto);
        UserDto newUserdto = UserDto.builder()
                .name("holland")
                .email("monkey@email.com")
                .build();
        UserDto updatedUser = userService.patch(newUserdto, savedUser.getId());

        assertThat(updatedUser.getId(), is(savedUser.getId()));
        assertThat(updatedUser.getName(), is(newUserdto.getName()));
        assertThat(updatedUser.getEmail(), is(newUserdto.getEmail()));
    }

    @Test
    void findUserById() {
        UserDto savedUser = userService.save(userDto);
        UserDto foundUser = userService.get(savedUser.getId());

        assertThat(foundUser.getId(), is(savedUser.getId()));
        assertThat(foundUser.getName(), is(savedUser.getName()));
        assertThat(foundUser.getEmail(), is(savedUser.getEmail()));
    }

    @Test
    void getAllUsers() {
        UserDto savedUser = userService.save(userDto);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users, is(List.of(savedUser)));
    }

    @Test
    void deleteUserById() {
        UserDto savedUser = userService.save(userDto);

        userService.delete(savedUser.getId());
        List<UserDto> users = userService.getAllUsers();
        assertEquals(0, users.size());
    }
}