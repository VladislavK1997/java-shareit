package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_ShouldMapCorrectly() {
        User user = createUser(1L, "John Doe", "john@email.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@email.com", dto.getEmail());
    }

    @Test
    void toUserDto_ShouldReturnNullForNullInput() {
        assertNull(UserMapper.toUserDto(null));
    }

    @Test
    void toUser_ShouldMapCorrectly() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john@email.com");

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@email.com", user.getEmail());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}