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
}