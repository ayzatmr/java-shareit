package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicInteger uniqueId = new AtomicInteger();


    @Override
    public List<Item> findByUserId(long userId) {
        return items.values()
                .stream()
                .filter(i -> i.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> get(long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public Optional<Item> save(Item item) {
        item.setId((long) uniqueId.incrementAndGet());
        item.setItemStatus(ItemStatus.AVAILABLE);
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> patch(Item item) {
        Optional<Item> currentItem = get(item.getId());
        if (currentItem.isPresent()) {
            items.put(item.getId(), item);
            return currentItem;
        }
        return Optional.of(item);
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return items.values()
                .stream()
                .filter(i -> (i.getName().contains(text) || i.getDescription().contains(text))
                        && i.getItemStatus() == ItemStatus.AVAILABLE)
                .distinct()
                .collect(Collectors.toList());
    }
}