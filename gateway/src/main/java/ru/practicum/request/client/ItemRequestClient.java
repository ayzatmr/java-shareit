package ru.practicum.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.httpClient.BaseHttpClient;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.NewItemRequestDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseHttpClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ItemRequestDto create(Long userId, NewItemRequestDto addItemRequestDto) {
        return post("", userId, addItemRequestDto, ItemRequestDto.class);
    }

    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        return get("", userId, List.class);
    }

    public List<ItemRequestDto> getAvailableItemRequests(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters, List.class);
    }

    public ItemRequestDto get(Long userId, Long requestId) {
        return get("/" + requestId, userId, ItemRequestDto.class);
    }
}
