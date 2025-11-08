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
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking_ShouldCreateBooking() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Description", true, owner.getId(), null));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void getBookingsByBooker_ShouldReturnBookings() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));
        Item item = itemRepository.save(new Item(null, "Item", "Description", true, owner.getId(), null));

        // Создаем бронирование
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
}