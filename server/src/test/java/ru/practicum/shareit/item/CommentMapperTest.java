package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto_ShouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Comment comment = new Comment(1L, "Test comment", new Item(), author, LocalDateTime.now());

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertEquals("Author", dto.getAuthorName());
        assertNotNull(dto.getCreated());
    }

    @Test
    void toCommentDto_ShouldReturnNullForNullInput() {
        assertNull(CommentMapper.toCommentDto(null));
    }
}