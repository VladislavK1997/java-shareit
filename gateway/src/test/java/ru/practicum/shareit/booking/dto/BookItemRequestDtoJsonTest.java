package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserializeBookItemRequestDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 2, 10, 0);

        BookItemRequestDto originalDto = new BookItemRequestDto(1L, start, end);

        String json = objectMapper.writeValueAsString(originalDto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2023-12-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-12-02T10:00:00\"");

        BookItemRequestDto deserializedDto = objectMapper.readValue(json, BookItemRequestDto.class);

        assertThat(deserializedDto.getItemId()).isEqualTo(1L);
        assertThat(deserializedDto.getStart()).isEqualTo(start);
        assertThat(deserializedDto.getEnd()).isEqualTo(end);
    }
}