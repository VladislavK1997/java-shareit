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

    @Test
    void toComment_ShouldMapCorrectly() {
        CommentDto dto = new CommentDto();
        dto.setText("Test comment");
        dto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toComment(dto);

        assertNotNull(comment);
        assertEquals("Test comment", comment.getText());
        assertNotNull(comment.getCreated());
    }

    @Test
    void toComment_ShouldSetCurrentTimeWhenCreatedIsNull() {
        CommentDto dto = new CommentDto();
        dto.setText("Test comment");
        dto.setCreated(null);

        Comment comment = CommentMapper.toComment(dto);

        assertNotNull(comment);
        assertNotNull(comment.getCreated());
    }
}