package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        UserDto userDto = new UserDto(null, "John Doe", "john@email.com");
        User user = new User(1L, "John Doe", "john@email.com");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowConflictException() {
        UserDto userDto = new UserDto(null, "John Doe", "john@email.com");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldThrowValidationException() {
        UserDto userDto = new UserDto(null, "John Doe", "invalid-email");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        User existingUser = new User(1L, "Old Name", "old@email.com");
        UserDto updateDto = new UserDto(null, "New Name", "new@email.com");
        User updatedUser = new User(1L, "New Name", "new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowNotFoundException() {
        UserDto updateDto = new UserDto(null, "New Name", "new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void getById_WithExistingId_ShouldReturnUser() {
        User user = new User(1L, "John Doe", "john@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        User user1 = new User(1L, "User 1", "user1@email.com");
        User user2 = new User(2L, "User 2", "user2@email.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
    }

    @Test
    void delete_WithExistingId_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}