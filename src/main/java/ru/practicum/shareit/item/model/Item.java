package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;

    private Long userId;

    private String url;

    private String name;

    private String description;

    private User owner;

    private ItemStatus itemStatus;
}
