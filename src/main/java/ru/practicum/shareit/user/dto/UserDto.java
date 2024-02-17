package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
public class UserDto {
    private Long id;

    @Email(message = "email should be valid")
    @NotEmpty(message = "email can not be empty")
    private String email;

    @NotEmpty(message = "name can not be empty")
    private String name;
}