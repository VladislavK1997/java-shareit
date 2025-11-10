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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@email.com");

        User user = createUser(1L, "John Doe", "john@email.com");

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
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@email.com");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldThrowValidationException() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_WithBlankName_ShouldThrowValidationException() {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("john@email.com");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_WithBlankEmail_ShouldThrowValidationException() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void getById_WithExistingId_ShouldReturnUser() {
        User user = createUser(1L, "John Doe", "john@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getById_WithNonExistentId_ShouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        User existingUser = createUser(1L, "Old Name", "old@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateOnlyName() {
        User existingUser = createUser(1L, "Old Name", "email@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("email@email.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail() {
        User existingUser = createUser(1L, "Name", "old@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Name", result.getName());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldThrowWhenDuplicateEmail() {
        User existingUser = createUser(1L, "User", "user@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("existing@email.com", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void updateUser_ShouldThrowWhenUserNotFound() {
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void getAll_ShouldReturnUsers() {
        User user1 = createUser(1L, "User1", "user1@email.com");
        User user2 = createUser(2L, "User2", "user2@email.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAll_ShouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_WithSameEmail_ShouldUpdateSuccessfully() {
        User existingUser = createUser(1L, "Old Name", "user@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("user@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("user@email.com", result.getEmail());
    }

    @Test
    void createUser_WithNullName_ShouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setName(null);
        userDto.setEmail("john@email.com");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_WithNullEmail_ShouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail(null);

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void updateUser_WithInvalidEmail_ShouldThrowException() {
        User existingUser = createUser(1L, "User", "user@email.com");
        UserDto updateDto = new UserDto();
        updateDto.setEmail("invalid");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.update(1L, updateDto));
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}