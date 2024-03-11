package ru.practicum.shareit.common.model;

import org.springframework.data.domain.Sort;

public class Constants {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    public static final Sort DEFAULT_SORTING = Sort.by(Sort.Direction.DESC, "start");
}
