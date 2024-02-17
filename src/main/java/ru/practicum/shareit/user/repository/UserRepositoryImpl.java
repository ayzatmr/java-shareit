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
        boolean present = users.values().stream()
                .anyMatch(i -> i.getEmail().equals(user.getEmail()));
        if (present) {
            return Optional.empty();
        }
        user.setId((long) uniqueId.incrementAndGet());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> currentUser = get(user.getId());
        if (currentUser.isPresent()) {
            users.put(user.getId(), user);
            return Optional.of(user);
        }
        return currentUser;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> get(Long userId) {
        return Optional.of(users.get(userId));
    }
}
