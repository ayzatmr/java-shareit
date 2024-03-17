package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    public static final Sort REQUEST_SORTING = Sort.by("created").descending();

    @Override
    public ItemRequestDto create(Long userId, NewItemRequestDto requestDto) {
        User user = getUser(userId);
        ItemRequest itemRequest = itemRequestMapper.toModel(requestDto);
        itemRequest.setUser(user);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        getUser(userId);
        List<ItemRequest> requests = itemRequestRepository.getUserItemRequests(userId, REQUEST_SORTING);
        return itemRequestMapper.toDtoList(requests);
    }

    @Override
    public List<ItemRequestDto> getAvailableItemRequests(Long userId, int from, int size) {
        getUser(userId);
        Pageable pageRequest = PageRequest.of(from, size, REQUEST_SORTING);
        Page<ItemRequest> requests = itemRequestRepository.findAvailableRequests(userId, pageRequest);
        return itemRequestMapper.toDtoList(requests.getContent());
    }

    @Override
    public ItemRequestDto get(Long userId, Long requestId) {
        getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request is not found"));
        return itemRequestMapper.toDto(itemRequest);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User is not found"));
    }
}