package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserializeUserDto() throws Exception {
        UserDto originalDto = new UserDto();
        originalDto.setId(1L);
        originalDto.setName("John Doe");
        originalDto.setEmail("john@email.com");

        String json = objectMapper.writeValueAsString(originalDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john@email.com\"");

        UserDto deserializedDto = objectMapper.readValue(json, UserDto.class);

        assertThat(deserializedDto.getId()).isEqualTo(1L);
        assertThat(deserializedDto.getName()).isEqualTo("John Doe");
        assertThat(deserializedDto.getEmail()).isEqualTo("john@email.com");
    }
}