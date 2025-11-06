package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplAdditionalTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_ShouldThrowWhenEmailIsInvalid() {
        UserDto userDto = new UserDto(null, "John Doe", "invalid-email");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void create_ShouldThrowWhenNameIsNull() {
        UserDto userDto = new UserDto(null, null, "john@email.com");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void update_ShouldThrowWhenEmailIsInvalid() {
        User existingUser = new User(1L, "Name", "old@email.com");
        UserDto updateDto = new UserDto(null, null, "invalid-email");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void getAll_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_ShouldNotThrowWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository).deleteById(1L);
    }
}