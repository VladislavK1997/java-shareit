package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingResponseDto_WithValidBooking_ShouldMapCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@email.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 12, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2023, 12, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingResponseDto dto = BookingMapper.toBookingResponseDto(booking);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
        assertNotNull(dto.getBooker());
        assertNotNull(dto.getItem());
    }

    @Test
    void toBookingResponseDto_WithNullBooking_ShouldReturnNull() {
        BookingResponseDto dto = BookingMapper.toBookingResponseDto(null);
        assertNull(dto);
    }
}