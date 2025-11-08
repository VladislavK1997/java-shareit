package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@email.com");

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnUsers() {
        User user1 = new User(null, "User 1", "user1@email.com");
        User user2 = new User(null, "User 2", "user2@email.com");
        userRepository.save(user1);
        userRepository.save(user2);

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }
}