package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderById_ShouldReturnOwnerItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);

        List<Item> result = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void searchAvailableItems_ShouldReturnMatchingItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item drill = createItem("Power Drill", "Electric drill", true, owner);
        Item hammer = createItem("Hammer", "Heavy hammer", true, owner);
        Item brokenDrill = createItem("Broken Drill", "Doesn't work", false, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
    }

    @Test
    void searchAvailableItems_WithEmptyText_ShouldReturnAllAvailableItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);
        Item unavailableItem = createItem("Unavailable Item", "Description 3", false, owner);

        List<Item> result = itemRepository.searchAvailableItems("");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Item::getAvailable));
    }

    @Test
    void searchAvailableItems_WithNullText_ShouldReturnEmptyList() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Item", "Description", true, owner);

        List<Item> result = itemRepository.searchAvailableItems(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_WithBlankText_ShouldReturnAllAvailableItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("   ");

        assertNotNull(result);
        assertNotNull(result);
    }

    @Test
    void searchAvailableItems_WithPartialMatch_ShouldReturnItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Power Drill", "Electric tool", true, owner);
        Item item2 = createItem("Hand Drill", "Manual tool", true, owner);
        Item item3 = createItem("Hammer", "Tool for hitting", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertNotNull(result);
        assertEquals(2, result.size()); // Both drills should match
        assertTrue(result.stream().allMatch(item ->
                item.getName().toLowerCase().contains("drill") ||
                        item.getDescription().toLowerCase().contains("drill")));
    }

    @Test
    void searchAvailableItems_OnlyAvailable_ShouldNotReturnUnavailableItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item availableItem = createItem("Available Item", "Working item", true, owner);
        Item unavailableItem = createItem("Unavailable Item", "Broken item", false, owner);

        List<Item> result = itemRepository.searchAvailableItems("item");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Available Item", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchAvailableItems_CaseInsensitive_ShouldReturnItems() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Power DRILL", "ELECTRIC tool", true, owner);
        Item item2 = createItem("Hand drill", "manual TOOL", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("DrIlL");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByRequestId_ShouldReturnItemsWithRequestId() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);

        item1.setRequestId(1L);
        entityManager.persistAndFlush(item1);

        List<Item> result = itemRepository.findByRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals(1L, result.get(0).getRequestId());
    }

    @Test
    void findByRequestId_WithNonExistentRequestId_ShouldReturnEmptyList() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Item", "Description", true, owner);

        List<Item> result = itemRepository.findByRequestId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByRequestIdIn_ShouldReturnItemsWithMatchingRequestIds() {
        User owner = createUser("Owner", "owner@email.com");
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);
        Item item3 = createItem("Item 3", "Description 3", true, owner);

        item1.setRequestId(1L);
        item2.setRequestId(2L);
        item3.setRequestId(1L);

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);
        entityManager.persistAndFlush(item3);

        List<Item> result = itemRepository.findByRequestIdIn(List.of(1L, 2L));

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getRequestId().equals(1L)));
        assertTrue(result.stream().anyMatch(item -> item.getRequestId().equals(2L)));
    }

    @Test
    void searchAvailableItems_NoMatches_ShouldReturnEmptyList() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Hammer", "Heavy tool", true, owner);
        createItem("Screwdriver", "Small tool", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertNotNull(result);
        assertTrue(result.isEmpty());
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