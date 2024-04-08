package ru.practicum.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.httpClient.BaseHttpClient;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Service
public class UserClient extends BaseHttpClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public UserDto save(UserDto userDto) {
        return post("", userDto, UserDto.class);
    }

    public UserDto patch(long userId, UserDto userDto) {
        return patch("/" + userId, userDto, UserDto.class);
    }

    public UserDto get(long userId) {
        return get("/" + userId, UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        return get("", List.class);
    }

    public void delete(long userId) {
        delete("/" + userId);
    }
}
