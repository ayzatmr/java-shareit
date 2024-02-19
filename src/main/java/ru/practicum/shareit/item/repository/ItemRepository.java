package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository {

    List<Item> findByUserId(long userId);

    Optional<Item> get(long itemId);

    Item save(Item item);

    Item patch(Item item);

    void delete(long itemId);

    List<Item> search(String text);
}
