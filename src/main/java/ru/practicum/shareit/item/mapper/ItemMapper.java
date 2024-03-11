package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);

    List<ItemDto> toDtoList(List<Item> item);

    Item toModel(NewItemDto itemDto);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemBookingDto toItemBookingDto(Booking booking);
}