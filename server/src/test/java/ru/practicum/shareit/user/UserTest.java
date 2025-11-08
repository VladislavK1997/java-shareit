package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setName("User2");
        user2.setEmail("user2@email.com");


        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        User user3 = new User();
        user3.setId(3L);

        assertNotEquals(user1, user3);
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "User", "user@email.com");
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("User", user.getName());
    }
}