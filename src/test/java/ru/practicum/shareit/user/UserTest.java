package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1L, "User1", "user1@email.com");
        User user2 = new User(1L, "User2", "user2@email.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        User user3 = new User(3L, "User1", "user1@email.com");
        assertNotEquals(user1, user3);
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
    }
}