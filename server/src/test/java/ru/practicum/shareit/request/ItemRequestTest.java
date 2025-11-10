package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        ItemRequest request3 = new ItemRequest();
        request3.setId(3L);
        assertNotEquals(request1, request3);
    }

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();
        assertNotNull(request);
    }
}