package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByItemIdOrderByCreatedDesc_ShouldReturnCommentsForItem() {
        User author = createUser("Author", "author@email.com");
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem("Item", "Description", true, owner);

        createComment("Old comment", item, author, LocalDateTime.now().minusDays(1));
        createComment("New comment", item, author, LocalDateTime.now());

        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("New comment", result.get(0).getText());
        assertEquals("Old comment", result.get(1).getText());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(owner.getId());
        return entityManager.persistAndFlush(item);
    }

    private Comment createComment(String text, Item item, User author, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return entityManager.persistAndFlush(comment);
    }
}