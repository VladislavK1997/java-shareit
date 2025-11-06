package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create_WithValidData_ShouldCreateRequest() {
        User requester = userRepository.save(new User(null, "Requester", "requester@email.com"));
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        ItemRequestDto result = itemRequestService.create(requestDto, requester.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void getByRequester_ShouldReturnUserRequests() {
        User requester = userRepository.save(new User(null, "Requester", "requester@email.com"));

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setRequester(requester);
        request1.setCreated(java.time.LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setRequester(requester);
        request2.setCreated(java.time.LocalDateTime.now());
        itemRequestRepository.save(request2);

        List<ItemRequestDto> result = itemRequestService.getByRequester(requester.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Request 2", result.get(0).getDescription());
        assertEquals("Request 1", result.get(1).getDescription());
    }

    @Test
    void getById_WithExistingId_ShouldReturnRequest() {
        User requester = userRepository.save(new User(null, "Requester", "requester@email.com"));
        User user = userRepository.save(new User(null, "User", "user@email.com"));

        ItemRequest request = new ItemRequest();
        request.setDescription("Test request");
        request.setRequester(requester);
        request.setCreated(java.time.LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);

        ItemRequestDto result = itemRequestService.getById(savedRequest.getId(), user.getId());

        assertNotNull(result);
        assertEquals(savedRequest.getId(), result.getId());
        assertEquals("Test request", result.getDescription());
    }

    @Test
    void getById_WithNonExistentId_ShouldThrowNotFoundException() {
        User user = userRepository.save(new User(null, "User", "user@email.com"));

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getById(999L, user.getId()));
    }

    @Test
    void getAll_ShouldReturnOtherUsersRequests() {
        User user1 = userRepository.save(new User(null, "User1", "user1@email.com"));
        User user2 = userRepository.save(new User(null, "User2", "user2@email.com"));

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request from user1");
        request1.setRequester(user1);
        request1.setCreated(java.time.LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request from user2");
        request2.setRequester(user2);
        request2.setCreated(java.time.LocalDateTime.now());
        itemRequestRepository.save(request2);

        List<ItemRequestDto> result = itemRequestService.getAll(user1.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request from user2", result.get(0).getDescription());
    }
}