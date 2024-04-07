package ru.practicum.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.client.ItemClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.NewCommentDto;
import ru.practicum.item.dto.NewItemDto;
import ru.practicum.user.dto.Create;
import ru.practicum.user.dto.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

import static ru.practicum.common.model.Constants.*;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) long userId,
                                 @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                 @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        return itemClient.findAll(userId, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId,
                       @Validated(Create.class) @RequestBody NewItemDto item) {
        return itemClient.add(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @Positive @PathVariable long itemId) {
        itemClient.delete(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(USER_HEADER) long userId,
                       @Positive @PathVariable long itemId) {
        return itemClient.get(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(USER_HEADER) long userId,
                             @Positive @PathVariable long itemId,
                             @Validated(Update.class) @RequestBody NewItemDto itemDto) {
        return itemClient.patch(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestHeader(USER_HEADER) long userId,
                                @RequestParam(defaultValue = MIN_SIZE) @PositiveOrZero int from,
                                @RequestParam(defaultValue = MAX_SIZE) @Positive int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable Long itemId,
                                       @Valid @RequestBody NewCommentDto commentDto) {
        return itemClient.addCommentToItem(userId, itemId, commentDto);
    }
}