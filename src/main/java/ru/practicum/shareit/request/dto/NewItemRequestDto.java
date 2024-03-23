package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewItemRequestDto {

    @NotBlank(message = "description can not bu null")
    @Size(max = 2000, message = "max size can not be more than 2000")
    private String description;
}