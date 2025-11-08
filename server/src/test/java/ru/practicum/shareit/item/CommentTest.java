package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());

        Comment comment3 = new Comment();
        comment3.setId(3L);
        assertNotEquals(comment1, comment3);
    }

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();
        assertNotNull(comment);
    }
}