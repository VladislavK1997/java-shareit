package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_ShouldCreateRequest() {
        User requester = new User(1L, "Requester", "requester@email.com");
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        ItemRequestDto result = itemRequestService.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
    }

    @Test
    void create_ShouldThrowWhenUserNotFound() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(requestDto, 1L));
    }

    @Test
    void getById_ShouldThrowWhenRequestNotFound() {
        User user = new User(1L, "User", "user@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(999L, 1L));
    }

    @Test
    void getById_ShouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 1L));
    }
}