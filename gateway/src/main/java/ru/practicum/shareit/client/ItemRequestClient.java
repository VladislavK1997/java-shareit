package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createRequest(long userId, ItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getByRequester(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}