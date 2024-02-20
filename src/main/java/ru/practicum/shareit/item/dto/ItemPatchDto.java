package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemPatchDto {
    @Size(max = 100, message = "max name size is 100")
    private String name;

    @Size(max = 500, message = "max description size is 500")
    private String description;

    private Boolean available;
}