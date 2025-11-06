package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findByRequesterIdOrderByCreatedDesc_ShouldReturnUserRequests() {
        User requester = createUser("Requester", "requester@email.com");
        ItemRequest request1 = createRequest("Need drill", requester, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = createRequest("Need hammer", requester, LocalDateTime.now());

        List<ItemRequest> result = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Need hammer", result.get(0).getDescription()); // Newest first
        assertEquals("Need drill", result.get(1).getDescription());
    }

    @Test
    void findByRequesterIdNotOrderByCreatedDesc_ShouldReturnOtherUsersRequests() {
        User user1 = createUser("User 1", "user1@email.com");
        User user2 = createUser("User 2", "user2@email.com");
        ItemRequest request1 = createRequest("Request 1", user1, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = createRequest("Request 2", user2, LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);

        List<ItemRequest> result = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request 2", result.get(0).getDescription());
        assertEquals(user2.getId(), result.get(0).getRequester().getId());
    }

    @Test
    void findAllByRequesterIdNot_ShouldReturnOtherUsersRequests() {
        User user1 = createUser("User 1", "user1@email.com");
        User user2 = createUser("User 2", "user2@email.com");
        ItemRequest request1 = createRequest("Request 1", user1, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = createRequest("Request 2", user2, LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);

        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdNot(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request 2", result.get(0).getDescription());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private ItemRequest createRequest(String description, User requester, LocalDateTime created) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(created);
        return entityManager.persistAndFlush(request);
    }
}