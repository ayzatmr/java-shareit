package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;

import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.common.model.Constants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId,
                       @Validated(Create.class) @RequestBody ItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @Positive @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@Positive @PathVariable long itemId) {
        return itemService.get(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(USER_HEADER) long userId,
                             @Positive @PathVariable long itemId,
                             @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemService.patchItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}