package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingShortDtoTest {

    @Test
    void shouldCreateAndAccessFields() {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(1L);
        dto.setBookerId(2L);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getBookerId());
    }
}