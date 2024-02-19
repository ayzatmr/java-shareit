package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;

    @Email(message = "email should be valid", groups = {Update.class, Create.class})
    @NotEmpty(message = "email can not be empty", groups = {Create.class})
    private String email;

    @NotBlank(message = "name can not be blank", groups = {Create.class})
    private String name;
}