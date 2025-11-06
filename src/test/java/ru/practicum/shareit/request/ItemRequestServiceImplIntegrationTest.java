package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void create_ShouldCreateItemRequestWithItems() {
        User requester = userRepository.save(new User(null, "Requester", "requester@email.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a power drill");

        ItemRequestDto result = itemRequestService.create(requestDto, requester.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Need a power drill", result.getDescription());
        assertNotNull(result.getCreated());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getByRequester_ShouldReturnRequestsWithItems() {
        User requester = userRepository.save(new User(null, "Requester", "requester@email.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a hammer");
        request.setRequester(requester);
        request.setCreated(java.time.LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);

        Item item = new Item();
        item.setName("Hammer");
        item.setDescription("Heavy hammer");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        item.setRequestId(savedRequest.getId());
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getByRequester(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a hammer", result.get(0).getDescription());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Hammer", result.get(0).getItems().get(0).getName());
    }

    @Test
    void getAll_WithPagination_ShouldReturnPaginatedResults() {
        User user1 = userRepository.save(new User(null, "User1", "user1@email.com"));
        User user2 = userRepository.save(new User(null, "User2", "user2@email.com"));

        for (int i = 0; i < 5; i++) {
            ItemRequest request = new ItemRequest();
            request.setDescription("Request " + i);
            request.setRequester(i % 2 == 0 ? user1 : user2);
            request.setCreated(java.time.LocalDateTime.now().minusDays(i));
            itemRequestRepository.save(request);
        }

        List<ItemRequestDto> result = itemRequestService.getAll(user1.getId(), 0, 3);

        assertNotNull(result);
        assertTrue(result.size() <= 3);
    }

    private Long getRequesterIdFromItems(ItemRequestDto dto) {
        return null;
    }
}