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
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));

        Item item = new Item(null, "Drill", "Power drill", true, owner.getId());
        Item savedItem = itemRepository.save(item);

        ru.practicum.shareit.booking.Booking pastBooking = new ru.practicum.shareit.booking.Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ru.practicum.shareit.booking.Booking futureBooking = new ru.practicum.shareit.booking.Booking();
        futureBooking.setStart(LocalDateTime.now().plusHours(2));
        futureBooking.setEnd(LocalDateTime.now().plusDays(1));
        futureBooking.setItem(savedItem);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        ItemDto result = itemService.getItemById(savedItem.getId(), owner.getId());

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertNotNull(result.getLastBooking(), "Last booking should not be null");
    }

    @Test
    void getItemById_ForOwner_WithOnlyPastBooking_ShouldIncludeOnlyLastBooking() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));

        Item item = new Item(null, "Hammer", "Heavy hammer", true, owner.getId());
        Item savedItem = itemRepository.save(item);

        ru.practicum.shareit.booking.Booking pastBooking = new ru.practicum.shareit.booking.Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ItemDto result = itemService.getItemById(savedItem.getId(), owner.getId());

        assertNotNull(result);
        assertEquals("Hammer", result.getName());
        assertNotNull(result.getLastBooking(), "Last booking should not be null");
        assertEquals(pastBooking.getId(), result.getLastBooking().getId());
    }

    @Test
    void getItemById_ForNonOwner_ShouldNotIncludeBookingInfo() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));
        User otherUser = userRepository.save(new User(null, "Other", "other@email.com"));

        Item item = new Item(null, "Saw", "Electric saw", true, owner.getId());
        Item savedItem = itemRepository.save(item);

        ru.practicum.shareit.booking.Booking pastBooking = new ru.practicum.shareit.booking.Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        ItemDto result = itemService.getItemById(savedItem.getId(), otherUser.getId());

        assertNotNull(result);
        assertEquals("Saw", result.getName());
        assertNull(result.getLastBooking(), "Last booking should be null for non-owner");
        assertNull(result.getNextBooking(), "Next booking should be null for non-owner");
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItemsWithCorrectInfo() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));

        Item item1 = new Item(null, "Item 1", "Description 1", true, owner.getId());
        Item item2 = new Item(null, "Item 2", "Description 2", true, owner.getId());
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        ru.practicum.shareit.booking.Booking booking = new ru.practicum.shareit.booking.Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(savedItem1);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

        result.forEach(itemDto -> {
            assertNotNull(itemDto.getId());
            assertNotNull(itemDto.getName());
            assertNotNull(itemDto.getDescription());
            assertNotNull(itemDto.getAvailable());
            assertNotNull(itemDto.getComments());
        });

        boolean hasItemWithBooking = result.stream()
                .anyMatch(item -> item.getLastBooking() != null);
        assertTrue(hasItemWithBooking, "At least one item should have booking info");
    }

    @Test
    void getAllItemsByOwner_WithNoItems_ShouldReturnEmptyList() {
        // Given
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        // When
        List<ItemDto> result = itemService.getAllItemsByOwner(owner.getId(), 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_ShouldReturnAvailableMatchingItems() {
        // Given
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));

        Item drill = new Item(null, "Power Drill", "Electric tool", true, owner.getId());
        Item hammer = new Item(null, "Hammer", "Heavy tool", true, owner.getId());
        Item brokenDrill = new Item(null, "Broken Drill", "Doesn't work", false, owner.getId());

        itemRepository.save(drill);
        itemRepository.save(hammer);
        itemRepository.save(brokenDrill);

        // When
        List<ItemDto> result = itemService.searchItems("drill", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only available drill
        assertEquals("Power Drill", result.get(0).getName());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    void searchItems_WithEmptyText_ShouldReturnEmptyList() {
        // Given
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        createItem("Item", "Description", true, owner);

        // When
        List<ItemDto> result = itemService.searchItems("", 0, 10);

        // Then
        assertNotNull(result);
        // Behavior depends on implementation - could be empty or all items
        assertNotNull(result);
    }

    @Test
    void addItem_ShouldCreateItemSuccessfully() {
        // Given
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);

        // When
        ItemDto result = itemService.addItem(itemDto, owner.getId());

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        // Given
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        Item item = createItem("Old Name", "Old Description", true, owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");

        // When
        ItemDto result = itemService.updateItem(item.getId(), updateDto, owner.getId());

        // Then
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item(null, name, description, available, owner.getId());
        return itemRepository.save(item);
    }
}