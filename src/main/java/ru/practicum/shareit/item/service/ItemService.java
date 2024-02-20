package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(long userId, ItemDto item);

    List<ItemDto> getItems(long userId);

    ItemDto get(long itemId);

    void deleteItem(long userId, long itemId);

    ItemDto patchItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);
}
