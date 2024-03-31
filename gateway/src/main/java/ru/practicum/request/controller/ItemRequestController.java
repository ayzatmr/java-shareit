package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.client.ItemRequestClient;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.NewItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.model.Constants.*;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody @Valid NewItemRequestDto requestDto) {
        return itemRequestClient.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAvailableItemRequests(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
            @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return itemRequestClient.getAvailableItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader(USER_HEADER) Long userId,
                              @PathVariable Long requestId) {
        return itemRequestClient.get(userId, requestId);
    }
}