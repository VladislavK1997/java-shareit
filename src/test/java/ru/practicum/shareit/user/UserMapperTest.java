package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_ShouldMapCorrectly() {
        User user = new User(1L, "John Doe", "john@email.com");

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
        UserDto dto = new UserDto(1L, "John Doe", "john@email.com");

        User user = UserMapper.toUser(dto);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@email.com", user.getEmail());
    }

    @Test
    void toUser_ShouldReturnNullForNullInput() {
        assertNull(UserMapper.toUser(null));
    }
}