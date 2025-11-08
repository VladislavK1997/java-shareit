package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getItemById_ForOwner_ShouldIncludeBookingInfo() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem("Drill", "Power drill", true, owner);

        ru.practicum.shareit.booking.Booking pastBooking = new ru.practicum.shareit.booking.Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertNotNull(result.getLastBooking());
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItemsWithCorrectInfo() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Item 1", "Description 1", true, owner);
        createItem("Item 2", "Description 2", true, owner);

        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(itemDto -> {
            assertNotNull(itemDto.getId());
            assertNotNull(itemDto.getName());
            assertNotNull(itemDto.getDescription());
        });
    }

    @Test
    void searchItems_ShouldReturnAvailableMatchingItems() {
        User owner = createUser("Owner", "owner@email.com");
        createItem("Power Drill", "Electric tool", true, owner);
        createItem("Hammer", "Heavy tool", true, owner);
        createItem("Broken Drill", "Doesn't work", false, owner);

        List<ItemDto> result = itemService.searchItems("drill", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(owner.getId());
        return itemRepository.save(item);
    }
}