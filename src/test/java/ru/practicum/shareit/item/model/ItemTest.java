package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item(1L, "Item1", "Description1", true, 1L);
        Item item2 = new Item(1L, "Item2", "Description2", false, 2L);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        Item item3 = new Item(3L, "Item1", "Description1", true, 1L);
        assertNotEquals(item1, item3);
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();
        assertNotNull(item);
    }
}