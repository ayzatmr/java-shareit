package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.common.model.Constants.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) long userId,
                                 @RequestParam(defaultValue = MIN_SIZE) int from,
                                 @RequestParam(defaultValue = MAX_SIZE) int size) {
        return itemService.getItems(userId, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId,
                       @RequestBody NewItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(USER_HEADER) long userId,
                       @PathVariable long itemId) {
        return itemService.get(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(USER_HEADER) long userId,
                             @PathVariable long itemId,
                             @RequestBody NewItemDto itemDto) {
        return itemService.patchItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = MIN_SIZE) int from,
                                @RequestParam(defaultValue = MAX_SIZE) int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody NewCommentDto commentDto) {
        return itemService.addCommentToItem(userId, itemId, commentDto);
    }
}