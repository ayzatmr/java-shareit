package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.ItemStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotNull(message = "userId can not be null")
    private Long userId;

    @NotNull(message = "url can not be null")
    private String url;

    @NotNull(message = "name can not be null")
    @Size(max = 100, message = "max name size is 100")
    private String name;

    @Size(max = 500, message = "max description size is 500")
    private String description;

    private ItemStatus itemStatus;
}