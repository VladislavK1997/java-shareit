package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoSimpleTest {

    @Test
    void shouldCreateUserDto() {
        UserDto dto = new UserDto(1L, "John", "john@email.com");

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getName());
        assertEquals("john@email.com", dto.getEmail());
    }

    @Test
    void testNoArgsConstructor() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("John");
        dto.setEmail("john@email.com");

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getName());
        assertEquals("john@email.com", dto.getEmail());
    }
}