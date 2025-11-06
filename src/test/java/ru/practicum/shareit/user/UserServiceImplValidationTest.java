package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplValidationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_ShouldValidateEmailFormat() {
        String[] invalidEmails = {
                "invalid",
                "invalid@",
                "",
                "   ",
                null
        };

        for (String email : invalidEmails) {
            UserDto dto = new UserDto(null, "User", email);
            assertThrows(ValidationException.class, () -> userService.create(dto),
                    "Should throw for email: '" + email + "'");
        }

        String[] validEmails = {
                "valid@email.com",
                "test@test.ru",
                "user.name@domain.co.uk",
                "a@b.c"
        };

        for (String email : validEmails) {
            UserDto dto = new UserDto(null, "User", email);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(new User(1L, "User", email));

            assertDoesNotThrow(() -> userService.create(dto),
                    "Should not throw for email: '" + email + "'");
        }
    }

    @Test
    void create_ShouldValidateName() {
        String[] invalidNames = {
                "",
                "   ",
                null
        };

        for (String name : invalidNames) {
            UserDto dto = new UserDto(null, name, "valid@email.com");
            assertThrows(ValidationException.class, () -> userService.create(dto),
                    "Should throw for name: '" + name + "'");
        }

        UserDto validDto = new UserDto(null, "Valid Name", "valid@email.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Valid Name", "valid@email.com"));

        assertDoesNotThrow(() -> userService.create(validDto));
    }

    @Test
    void create_ShouldThrowWhenEmailExists() {
        UserDto dto = new UserDto(null, "User", "existing@email.com");
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(dto));
    }

    @Test
    void update_ShouldHandlePartialUpdates() {
        User existingUser = new User(1L, "Old Name", "old@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);


        UserDto updateName = new UserDto(null, "New Name", null);
        assertDoesNotThrow(() -> userService.update(1L, updateName));
        assertEquals("New Name", existingUser.getName());
        assertEquals("old@email.com", existingUser.getEmail()); // unchanged

        UserDto updateEmail = new UserDto(null, null, "new@email.com");
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> userService.update(1L, updateEmail));
        assertEquals("New Name", existingUser.getName()); // unchanged
        assertEquals("new@email.com", existingUser.getEmail());

        UserDto updateBoth = new UserDto(null, "Newer Name", "newer@email.com");
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> userService.update(1L, updateBoth));
        assertEquals("Newer Name", existingUser.getName());
        assertEquals("newer@email.com", existingUser.getEmail());
    }

    @Test
    void update_ShouldHandleNoChanges() {
        User existingUser = new User(1L, "Name", "email@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto noChanges = new UserDto(null, null, null);

        assertDoesNotThrow(() -> userService.update(1L, noChanges));
        assertEquals("Name", existingUser.getName()); // unchanged
        assertEquals("email@email.com", existingUser.getEmail()); // unchanged
    }

    @Test
    void update_ShouldThrowWhenEmailTaken() {
        User existingUser = new User(1L, "Name", "old@email.com");
        UserDto updateDto = new UserDto(null, null, "taken@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("taken@email.com", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void update_ShouldThrowWhenEmailInvalid() {
        User existingUser = new User(1L, "Name", "old@email.com");

        UserDto updateDto = new UserDto(null, null, "invalid-email");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.update(1L, updateDto));
    }
}