package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong uniqueId = new AtomicLong();


    @Override
    public List<Item> findByUserId(long userId) {
        return items.values()
                .stream()
                .filter(i -> i.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> get(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(Item item) {
        item.setId(uniqueId.incrementAndGet());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patch(Item item) {
        Optional<Item> currentItem = get(item.getId());
        if (currentItem.isPresent()) {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return items.values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(text)
                        || i.getDescription().toLowerCase().contains(text))
                        && i.getAvailable())
                .distinct()
                .collect(Collectors.toList());
    }
}
