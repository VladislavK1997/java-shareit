package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void addItem_WithValidData_ShouldReturnItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenReturn(responseDto);

        ItemDto result = itemController.addItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemService).addItem(itemDto, 1L);
    }

    @Test
    void updateItem_WithValidData_ShouldReturnUpdatedItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Updated Item");

        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(responseDto);

        ItemDto result = itemController.updateItem(1L, itemDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Item", result.getName());
        verify(itemService).updateItem(1L, itemDto, 1L);
    }

    @Test
    void getItemById_ShouldReturnItem() {
        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(responseDto);

        ItemDto result = itemController.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemService).getItemById(1L, 1L);
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItems() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemService.getAllItemsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        List<ItemDto> result = itemController.getAllItemsByOwner(1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        verify(itemService).getAllItemsByOwner(1L, 0, 10);
    }

    @Test
    void searchItems_WithValidText_ShouldReturnItems() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        List<ItemDto> result = itemController.searchItems("drill", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemService).searchItems("drill", 0, 10);
    }

    @Test
    void addComment_WithValidData_ShouldReturnComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);

        when(itemService.addComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(responseDto);

        CommentDto result = itemController.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemService).addComment(1L, commentDto, 1L);
    }
}