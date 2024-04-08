package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.AlreadyExistException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto user) {
        User userModel = userMapper.toModel(user);
        User currentUser = repository.save(userModel);
        return userMapper.toDto(currentUser);
    }

    @Override
    public UserDto patch(UserDto user, Long userId) {
        User userFromDB = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user is not found"));
        if (user.getName() != null && !user.getName().isBlank()) {
            userFromDB.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            Optional<UserDto> foundUser = getAllUsers()
                    .stream()
                    .filter(i -> i.getEmail().equals(user.getEmail()))
                    .findFirst();
            if (foundUser.isPresent()) {
                if (!foundUser.get().getId().equals(userId)) {
                    throw new AlreadyExistException("email should be unique");
                }
            }
            userFromDB.setEmail(user.getEmail());
        }
        User currentUser = repository.save(userFromDB);
        return userMapper.toDto(currentUser);
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public UserDto get(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user is not found"));
        return userMapper.toDto(user);
    }
}