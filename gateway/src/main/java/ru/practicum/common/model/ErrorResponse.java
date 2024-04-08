package ru.practicum.common.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class ErrorResponse {
    private final List<String> errors;
}
