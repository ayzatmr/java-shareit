package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(long userId, NewItemDto item);

    List<ItemDto> getItems(long userId, int from, int size);

    ItemDto get(long itemId, long userId);

    void deleteItem(long userId, long itemId);

    ItemDto patchItem(long userId, NewItemDto itemDto, long itemId);

    List<ItemDto> search(String text, int from, int size);

    CommentDto addCommentToItem(Long userId, Long itemId, NewCommentDto commentDto);
}
