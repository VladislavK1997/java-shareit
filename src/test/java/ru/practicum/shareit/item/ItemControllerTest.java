// 2. Тесты для ItemController с новой функциональностью requestId
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_WithRequestId_ShouldCreateItemWithRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Power Drill");
        itemDto.setDescription("Electric power drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Power Drill");
        responseDto.setDescription("Electric power drill");
        responseDto.setAvailable(true);
        responseDto.setRequestId(1L);

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestId").value(1L));
    }

    @Test
    void addItem_WithoutRequestId_ShouldCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Hammer");
        itemDto.setDescription("Heavy hammer");
        itemDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Hammer");
        responseDto.setDescription("Heavy hammer");
        responseDto.setAvailable(true);

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestId").doesNotExist());
    }
}