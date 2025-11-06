package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingResponseDto_WithValidBooking_ShouldMapCorrectly() {
        User booker = new User(1L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L);
        Booking booking = new Booking(1L,
                LocalDateTime.of(2023, 12, 1, 10, 0),
                LocalDateTime.of(2023, 12, 2, 10, 0),
                item, booker, BookingStatus.WAITING);

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

    @Test
    void toBooking_WithValidBookingDto_ShouldMapCorrectly() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2023, 12, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 12, 2, 10, 0));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertNotNull(booking);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void toBooking_WithNullStatus_ShouldSetWaitingStatus() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(null);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertNotNull(booking);
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}