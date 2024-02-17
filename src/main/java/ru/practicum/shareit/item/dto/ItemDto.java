package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotNull(message = "name can not be null")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "max name size is 100")
    private String name;

    @Size(max = 500, message = "max description size is 500")
    @NotNull(message = "description can not be null")
    @NotEmpty(message = "name can not be empty")
    private String description;

    @NotNull(message = "available can not be null")
    private Boolean available;
}