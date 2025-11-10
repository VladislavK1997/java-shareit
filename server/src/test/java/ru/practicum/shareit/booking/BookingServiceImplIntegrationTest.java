package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem("Item", "Description", true, owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result);
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void getBookingsByBooker_ShouldReturnBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem("Item", "Description", true, owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(bookingDto, booker.getId());

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(
                BookingState.ALL, booker.getId(), 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
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