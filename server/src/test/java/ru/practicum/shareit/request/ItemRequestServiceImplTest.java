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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Test
    void getAll_ShouldReturnEmptyListWhenNoRequests() {
        User user = createUser(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAll(1L, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByRequester_ShouldReturnEmptyListWhenNoRequests() {
        User user = createUser(1L, "User", "user@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByRequester_ShouldReturnRequestsWithItems() {
        User requester = createUser(1L, "Requester", "requester@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need item", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
    }

    @Test
    void getAll_ShouldReturnRequestsWithItems() {
        User user = createUser(1L, "User", "user@email.com");
        User requester = createUser(2L, "Requester", "requester@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAll(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need item", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
    }

    @Test
    void getById_ShouldReturnRequestWithItems() {
        User user = createUser(1L, "User", "user@email.com");
        User requester = createUser(2L, "Requester", "requester@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals("Need item", result.getDescription());
        assertNotNull(result.getItems());
    }

    @Test
    void enrichWithItems_ShouldReturnRequestWithEmptyItemsWhenNoItems() {
        User requester = createUser(1L, "Requester", "requester@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void enrichWithItems_ShouldReturnRequestWithItems() {
        User requester = createUser(1L, "Requester", "requester@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        ItemRequest request = new ItemRequest(1L, "Need item", requester, LocalDateTime.now());
        Item item = new Item(1L, "Item", "Description", true, owner.getId(), 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Item", result.getItems().get(0).getName());
    }

    @Test
    void create_ShouldSetCurrentTimeWhenCreatedIsNull() {
        User requester = createUser(1L, "Requester", "requester@email.com");
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need item");
        requestDto.setCreated(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        ItemRequestDto result = itemRequestService.create(requestDto, 1L);

        assertNotNull(result);
        assertNotNull(result.getCreated());
    }

    @Test
    void create_ShouldUseProvidedCreatedTime() {
        User requester = createUser(1L, "Requester", "requester@email.com");
        LocalDateTime createdTime = LocalDateTime.now().minusHours(1);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need item");
        requestDto.setCreated(createdTime);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        ItemRequestDto result = itemRequestService.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(createdTime, result.getCreated());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}