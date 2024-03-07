package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(long userId, ItemDto item);

    List<ItemDto> getItems(long userId);

    ItemDto get(long itemId, long userId);

    void deleteItem(long userId, long itemId);

    ItemDto patchItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
