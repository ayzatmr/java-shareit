package ru.practicum.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.httpClient.BaseHttpClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.NewCommentDto;
import ru.practicum.item.dto.NewItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseHttpClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ItemDto add(Long userId, NewItemDto itemDto) {
        return post("/", userId, itemDto, ItemDto.class);
    }

    public void delete(Long userId, Long itemId) {
        delete("/" + itemId, userId);
    }

    public ItemDto patch(Long userId, NewItemDto itemUpdateDto, long itemId) {
        return patch("/" + itemId, userId, itemUpdateDto, ItemDto.class);
    }

    public ItemDto get(long itemId, long userId) {
        return get("/" + itemId, userId, ItemDto.class);
    }

    public List<ItemDto> findAll(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters, List.class);
    }

    public List<ItemDto> search(Long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters, List.class);
    }

    public CommentDto addCommentToItem(Long userId, Long itemId, NewCommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto, CommentDto.class);
    }
}
