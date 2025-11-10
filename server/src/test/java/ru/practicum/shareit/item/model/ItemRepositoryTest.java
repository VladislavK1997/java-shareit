package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderById_ShouldReturnOwnerItems() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Item 1", "Description 1", true, owner);
        createItem("Item 2", "Description 2", true, owner);

        List<Item> result = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void searchAvailableItems_ShouldReturnMatchingItems() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Power Drill", "Electric drill", true, owner);
        createItem("Hammer", "Heavy hammer", true, owner);
        createItem("Broken Drill", "Doesn't work", false, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
    }

    @Test
    void findByRequestId_ShouldReturnItemsWithRequestId() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem("Item 1", "Description 1", true, owner);
        item.setRequestId(1L);
        entityManager.persistAndFlush(item);

        List<Item> result = itemRepository.findByRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals(1L, result.get(0).getRequestId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(owner.getId());
        return entityManager.persistAndFlush(item);
    }
}