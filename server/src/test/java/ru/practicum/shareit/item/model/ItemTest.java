package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEqualsAndHashCode() {
        Item item1 = createItem(1L, "Item1", "Description1", true, 1L);
        Item item2 = createItem(1L, "Item2", "Description2", false, 2L);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        Item item3 = createItem(3L, "Item1", "Description1", true, 1L);
        assertNotEquals(item1, item3);
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();
        assertNotNull(item);
    }

    private Item createItem(Long id, String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }
}