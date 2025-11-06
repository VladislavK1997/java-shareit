package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void shouldCreateAndAccessFields() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        BookingShortDto lastBooking = new BookingShortDto();
        lastBooking.setId(3L);
        dto.setLastBooking(lastBooking);

        BookingShortDto nextBooking = new BookingShortDto();
        nextBooking.setId(4L);
        dto.setNextBooking(nextBooking);

        CommentDto comment = new CommentDto();
        comment.setText("Comment");
        dto.setComments(List.of(comment));

        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(2L, dto.getRequestId());
        assertEquals(3L, dto.getLastBooking().getId());
        assertEquals(4L, dto.getNextBooking().getId());
        assertEquals(1, dto.getComments().size());
    }
}