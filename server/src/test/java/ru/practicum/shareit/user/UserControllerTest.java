package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void create_WithValidData_ShouldReturnUser() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@email.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);

        when(userService.create(any(UserDto.class))).thenReturn(responseDto);

        UserDto result = userController.create(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).create(userDto);
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Updated Name");

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(responseDto);

        UserDto result = userController.update(1L, userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        verify(userService).update(1L, userDto);
    }

    @Test
    void getById_ShouldReturnUser() {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);

        when(userService.getById(anyLong())).thenReturn(responseDto);

        UserDto result = userController.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).getById(1L);
    }

    @Test
    void getAll_ShouldReturnUsers() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userService.getAll()).thenReturn(List.of(userDto));

        List<UserDto> result = userController.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        verify(userService).getAll();
    }

    @Test
    void delete_ShouldCallService() {
        doNothing().when(userService).delete(anyLong());

        userController.delete(1L);

        verify(userService).delete(1L);
    }
}