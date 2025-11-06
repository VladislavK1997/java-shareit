package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserializeBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 2, 10, 0);

        BookingDto originalDto = new BookingDto();
        originalDto.setId(1L);
        originalDto.setStart(start);
        originalDto.setEnd(end);
        originalDto.setItemId(1L);
        originalDto.setStatus(BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(originalDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("\"start\":\"2023-12-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-12-02T10:00:00\"");

        BookingDto deserializedDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(deserializedDto.getId()).isEqualTo(1L);
        assertThat(deserializedDto.getItemId()).isEqualTo(1L);
        assertThat(deserializedDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(deserializedDto.getStart()).isEqualTo(start);
        assertThat(deserializedDto.getEnd()).isEqualTo(end);
    }
}