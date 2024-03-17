package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.common.model.Constants.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody @Valid NewItemRequestDto requestDto) {
        return itemRequestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAvailableItemRequests(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
            @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return itemRequestService.getAvailableItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader(USER_HEADER) Long userId,
                              @PathVariable Long requestId) {
        return itemRequestService.get(userId, requestId);
    }
}