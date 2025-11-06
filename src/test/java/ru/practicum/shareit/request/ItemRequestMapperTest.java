package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto_ShouldMapCorrectly() {
        User requester = new User(1L, "Requester", "requester@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need item", dto.getDescription());
        assertNotNull(dto.getCreated());
    }

    @Test
    void toItemRequestDto_ShouldReturnNullForNullInput() {
        assertNull(ItemRequestMapper.toItemRequestDto(null));
    }

    @Test
    void toItemRequest_ShouldMapCorrectly() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need item");
        dto.setCreated(LocalDateTime.now());

        ItemRequest request = ItemRequestMapper.toItemRequest(dto);

        assertNotNull(request);
        assertEquals("Need item", request.getDescription());
        assertNotNull(request.getCreated());
    }

    @Test
    void toItemRequest_ShouldSetCurrentTimeWhenCreatedIsNull() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need item");
        dto.setCreated(null);

        ItemRequest request = ItemRequestMapper.toItemRequest(dto);

        assertNotNull(request);
        assertNotNull(request.getCreated());
    }

    @Test
    void toItemRequest_ShouldReturnNullForNullInput() {
        assertNull(ItemRequestMapper.toItemRequest(null));
    }
}