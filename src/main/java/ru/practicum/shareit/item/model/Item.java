package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class Item {
    private Long id;

    private Long userId;

    private String url;

    private String name;

    private String description;

    private User owner;

    private ItemStatus itemStatus;
}
