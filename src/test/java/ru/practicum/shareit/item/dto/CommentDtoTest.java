package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentDtoTest {

    @Test
    void shouldCreateAndAccessFields() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Test comment");
        dto.setAuthorName("Author");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreated(now);

        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertEquals("Author", dto.getAuthorName());
        assertEquals(now, dto.getCreated());
    }
}