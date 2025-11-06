package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        User owner = new User(null, "Owner", "owner@email.com");
        User booker = new User(null, "Booker", "booker@email.com");
        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = new Item(null, "Item", "Description", true, savedOwner.getId());
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto result = bookingService.create(bookingDto, savedBooker.getId());

        assertNotNull(result);
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(savedBooker.getId(), result.getBooker().getId());
        assertEquals(savedItem.getId(), result.getItem().getId());
    }

    @Test
    void getBookingsByBooker_ShouldReturnBookings() {
        User owner = new User(null, "Owner", "owner@email.com");
        User booker = new User(null, "Booker", "booker@email.com");
        User savedOwner = userRepository.save(owner);
        User savedBooker = userRepository.save(booker);

        Item item = new Item(null, "Item", "Description", true, savedOwner.getId());
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(bookingDto, savedBooker.getId());

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(
                BookingStatus.ALL, savedBooker.getId(), 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}