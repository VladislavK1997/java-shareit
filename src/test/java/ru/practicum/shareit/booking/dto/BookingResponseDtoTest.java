package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BookingResponseDtoTest {

    @Test
    void shouldCreateAndAccessFields() {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setStatus(BookingStatus.APPROVED);

        assertEquals(1L, dto.getId());
        assertNotNull(dto.getStart());
        assertNotNull(dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
    }

    @Test
    void testDataAnnotation() {
        BookingResponseDto dto1 = new BookingResponseDto();
        dto1.setId(1L);

        BookingResponseDto dto2 = new BookingResponseDto();
        dto2.setId(1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}