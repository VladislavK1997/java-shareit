package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemDtoSimpleTest {

    @Test
    void shouldCreateItemDto() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(2L);
        dto.setComments(List.of());

        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(2L, dto.getRequestId());
        assertNotNull(dto.getComments());
    }
}