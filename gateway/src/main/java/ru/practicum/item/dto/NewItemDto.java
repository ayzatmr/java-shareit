package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.dto.Create;
import ru.practicum.user.dto.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewItemDto {
    private Long id;

    @NotBlank(message = "name can not be blank", groups = {Create.class})
    @Size(max = 100, message = "max name size is 100", groups = {Create.class, Update.class})
    private String name;

    @Size(max = 500, message = "max description size is 500", groups = {Create.class, Update.class})
    @NotBlank(message = "description can not be blank", groups = {Create.class})
    private String description;

    @NotNull(message = "available can not be null", groups = {Create.class})
    private Boolean available;

    @Positive(message = "requestId should be grater than 0")
    private Long requestId;
}