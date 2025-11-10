package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setOwnerId(1L);

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(false);
        item2.setOwnerId(2L);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        Item item3 = new Item();
        item3.setId(3L);

        assertNotEquals(item1, item3);
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();
        assertNotNull(item);
    }

    @Test
    void testAllArgsConstructor() {
        Item item = new Item(1L, "Item", "Description", true, 1L, 2L);
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Item", item.getName());
    }
}