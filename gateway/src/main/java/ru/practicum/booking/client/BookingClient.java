package ru.practicum.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.dto.NewBookingDto;
import ru.practicum.httpClient.BaseHttpClient;

import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseHttpClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public List<BookingDto> findAll(long userId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters, List.class);
    }

    public List<BookingDto> getAllOwnerBookings(long userId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters, List.class);
    }


    public BookingDto create(long userId, NewBookingDto bookingDto) {
        return post("", userId, bookingDto, BookingDto.class);
    }

    public BookingDto get(long userId, Long bookingId) {
        return get("/" + bookingId, userId, BookingDto.class);
    }

    public BookingDto patch(long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, BookingDto.class);
    }
}
