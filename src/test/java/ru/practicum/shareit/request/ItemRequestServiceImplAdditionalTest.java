package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplAdditionalTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void getById_ShouldThrowWhenRequestNotFound() {
        User user = new User(1L, "User", "user@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(999L, 1L));
    }

    @Test
    void getByRequester_ShouldReturnEmptyList() {
        User user = new User(1L, "User", "user@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_ShouldReturnEmptyList() {
        User user = new User(1L, "User", "user@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAll(1L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByRequester_ShouldHandleEmptyItems() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getItems());
        assertTrue(result.get(0).getItems().isEmpty());
    }

    @Test
    void getByRequester_ShouldHandleMultipleItems() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");

        Item item1 = new Item(1L, "Item1", "Desc1", true, 2L);
        item1.setRequestId(1L);
        Item item2 = new Item(2L, "Item2", "Desc2", true, 3L);
        item2.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item1, item2));

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getItems());
        assertEquals(2, result.get(0).getItems().size());
        assertEquals("Item1", result.get(0).getItems().get(0).getName());
        assertEquals("Item2", result.get(0).getItems().get(1).getName());
    }

    @Test
    void getById_ShouldIncludeItems() {
        User user = new User(1L, "User", "user@email.com");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");

        Item item = new Item(1L, "Item", "Description", true, 2L);
        item.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Item", result.getItems().get(0).getName());
    }
}