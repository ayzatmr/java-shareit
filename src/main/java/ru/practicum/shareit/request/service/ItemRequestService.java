package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, NewItemRequestDto requestDto);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getAvailableItemRequests(Long userId, int from, int size);

    ItemRequestDto get(Long userId, Long requestId);
}