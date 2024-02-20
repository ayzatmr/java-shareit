package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        checkUser(userId);
        return itemRepository.findByUserId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long itemId) {
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto item) {
        checkUser(userId);
        Item newItem = itemRepository.save(ItemMapper.toModel(item, userId));
        return ItemMapper.toDto(newItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        checkUser(userId);
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (!item.getUserId().equals(userId)) {
            throw new ValidationException("you can not delete that item");
        }
        itemRepository.delete(itemId);
    }

    @Override
    public ItemDto patchItem(long userId, ItemDto itemDto, long itemId) {
        checkUser(userId);
        Item itemFromDB = itemRepository.get(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (!itemFromDB.getUserId().equals(userId)) {
            throw new ObjectNotFoundException("you can not update that item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemFromDB.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemFromDB.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemFromDB.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.patch(itemFromDB);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.toLowerCase())
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkUser(long userId) {
        userRepository.get(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user is not found"));
    }
}
