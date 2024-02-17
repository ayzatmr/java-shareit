package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .url(item.getUrl())
                .name(item.getName())
                .description(item.getDescription())
                .itemStatus(item.getItemStatus())
                .build();
    }

    public static Item toModel(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .userId(itemDto.getUserId())
                .url(itemDto.getUrl())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .itemStatus(itemDto.getItemStatus())
                .build();
    }
}