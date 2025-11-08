package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_ShouldMapCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(2L);
        item.setRequestId(3L);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(3L, dto.getRequestId());
    }

    @Test
    void toItemDto_ShouldReturnNullForNullInput() {
        assertNull(ItemMapper.toItemDto(null));
    }

    @Test
    void toItem_ShouldMapCorrectly() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        Item item = ItemMapper.toItem(dto, 3L);

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Item", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(3L, item.getOwnerId());
        assertEquals(2L, item.getRequestId());
    }
}