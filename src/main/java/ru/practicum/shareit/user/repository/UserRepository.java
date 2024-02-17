package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> save(User user);

    Optional<User> update(User user);

    void delete(long userId);

    Optional<User> get(Long userId);
}