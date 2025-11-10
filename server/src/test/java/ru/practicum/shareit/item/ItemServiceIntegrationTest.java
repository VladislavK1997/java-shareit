package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addItem_ShouldAddItem() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto result = itemService.addItem(itemDto, owner.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItems() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        Item item1 = new Item(null, "Item 1", "Description 1", true, owner.getId(), null);
        Item item2 = new Item(null, "Item 2", "Description 2", true, owner.getId(), null);
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}