package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void from_ShouldReturnCorrectStatus() {
        assertEquals(BookingStatus.WAITING, BookingStatus.from("waiting"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.from("approved"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.from("rejected"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.from("canceled"));
    }

    @Test
    void from_ShouldBeCaseInsensitive() {
        assertEquals(BookingStatus.WAITING, BookingStatus.from("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.from("Approved"));
    }

    @Test
    void from_ShouldThrowForInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.from("invalid"));
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.from(""));
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.from(null));
    }

    @Test
    void values_ShouldReturnAllStatuses() {
        BookingStatus[] statuses = BookingStatus.values();

        assertTrue(statuses.length > 0);
        assertTrue(List.of(statuses).contains(BookingStatus.WAITING));
        assertTrue(List.of(statuses).contains(BookingStatus.APPROVED));
        assertTrue(List.of(statuses).contains(BookingStatus.REJECTED));
        assertTrue(List.of(statuses).contains(BookingStatus.CANCELED));
    }

    @Test
    void valueOf_ShouldReturnCorrectStatus() {
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }
}