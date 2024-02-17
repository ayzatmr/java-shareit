package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto user) {
        User userModel = UserMapper.toModel(user);
        User currentUser = repository.save(userModel)
                .orElseThrow(() -> new ValidationException("can not create user"));
        return UserMapper.toDto(currentUser);
    }

    @Override
    public UserDto patch(UserPatchDto user, Long userId) {
        User userToPatch = repository.get(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user is not found"));

        if (user.getName() != null) {
            userToPatch.setName(userToPatch.getName());
        }
        if (user.getEmail() != null) {
            userToPatch.setEmail(userToPatch.getEmail());
        }
        User currentUser = repository.patch(userToPatch).get();
        return UserMapper.toDto(currentUser);
    }

    @Override
    public void delete(long userId) {
        repository.delete(userId);
    }

    @Override
    public UserDto get(Long userId) {
        User user = repository.get(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user is not found"));
        return UserMapper.toDto(user);
    }
}