package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class ItemRequest {
    private Long id;

    @Size(max = 500, message = "max description size is 500")
    private String description;

    @NonNull
    private User requestor;

    @FutureOrPresent
    private LocalDateTime dateCreated;
}
