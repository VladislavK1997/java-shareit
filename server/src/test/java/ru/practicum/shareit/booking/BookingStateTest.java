package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_ShouldReturnCorrectState() {
        assertEquals(BookingState.ALL, BookingState.from("all"));
        assertEquals(BookingState.CURRENT, BookingState.from("current"));
        assertEquals(BookingState.PAST, BookingState.from("past"));
        assertEquals(BookingState.FUTURE, BookingState.from("future"));
        assertEquals(BookingState.WAITING, BookingState.from("waiting"));
        assertEquals(BookingState.REJECTED, BookingState.from("rejected"));
    }

    @Test
    void from_ShouldBeCaseInsensitive() {
        assertEquals(BookingState.ALL, BookingState.from("ALL"));
        assertEquals(BookingState.ALL, BookingState.from("All"));
        assertEquals(BookingState.WAITING, BookingState.from("Waiting"));
    }

    @Test
    void from_ShouldThrowForInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> BookingState.from("invalid"));
        assertThrows(IllegalArgumentException.class, () -> BookingState.from(""));
    }

    @Test
    void from_ShouldReturnAllForNull() {
        assertEquals(BookingState.ALL, BookingState.from(null));
    }
}