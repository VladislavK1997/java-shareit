package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
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
}