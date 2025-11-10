package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = createUser("John Doe", "john@email.com");

        Optional<User> result = userRepository.findByEmail("john@email.com");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@email.com", result.get().getEmail());
    }

    @Test
    void existsByEmail_ShouldReturnTrueForExistingEmail() {
        createUser("John Doe", "john@email.com");

        boolean exists = userRepository.existsByEmail("john@email.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnTrueForOtherUserWithSameEmail() {
        User user1 = createUser("User 1", "user1@email.com");
        User user2 = createUser("User 2", "user2@email.com");

        boolean exists = userRepository.existsByEmailAndIdNot("user2@email.com", user1.getId());

        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalseForSameUser() {
        User user = createUser("User", "user@email.com");

        boolean exists = userRepository.existsByEmailAndIdNot("user@email.com", user.getId());

        assertFalse(exists);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }
}