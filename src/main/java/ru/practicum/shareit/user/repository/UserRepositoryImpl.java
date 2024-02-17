package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private final AtomicInteger uniqueId = new AtomicInteger();

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> save(User user) {
        user.setId((long) uniqueId.incrementAndGet());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> patch(User user) {
        Optional<User> currentUser = get(user.getId());
        if (currentUser.isPresent()) {
            users.put(user.getId(), user);
        }
        return Optional.of(user);
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> get(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
