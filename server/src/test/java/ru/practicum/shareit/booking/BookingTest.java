package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testEqualsAndHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());

        Booking booking3 = new Booking();
        booking3.setId(3L);
        assertNotEquals(booking1, booking3);
    }

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking);
    }

    @Test
    void testAllArgsConstructor() {
        Booking booking = new Booking(1L, null, null, null, null, BookingStatus.WAITING);
        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}