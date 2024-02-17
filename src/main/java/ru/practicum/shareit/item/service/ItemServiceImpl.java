package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long itemId) {
        Item item = repository.get(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto item) {
        item.setUserId(userId);
        Item newItem = repository.save(ItemMapper.toModel(item))
                .orElseThrow(() -> new ValidationException("something went wrong"));
        return ItemMapper.toDto(newItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Item item = repository.get(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        if (!item.getUserId().equals(userId)) {
            throw new ValidationException("you can not delete that item");
        }
        repository.delete(itemId);
    }

    @Override
    public ItemDto patchItem(long userId, ItemPatchDto itemDto) {
        Item itemFromDB = repository.get(itemDto.getId())
                .orElseThrow(() -> new ObjectNotFoundException("item is not found"));
        Item itemToPatch = new Item().toBuilder()
                .id(itemFromDB.getId())
                .userId(itemFromDB.getUserId())
                .name(itemFromDB.getName())
                .description(itemFromDB.getDescription())
                .owner(itemFromDB.getOwner())
                .itemStatus(itemFromDB.getItemStatus())
                .build();

        if (!itemToPatch.getUserId().equals(userId)) {
            throw new ValidationException("you can not update that item");
        }
        if (itemDto.getName() != null) {
            itemToPatch.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToPatch.setDescription(itemDto.getDescription());
        }
        if (itemDto.getItemStatus() != null) {
            itemToPatch.setItemStatus(itemDto.getItemStatus());
        }
        Item updatedItem = repository.patch(itemToPatch).get();
        return ItemMapper.toDto(updatedItem);
    }


    @Override
    public List<ItemDto> search(String text) {
        return repository.search(text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
