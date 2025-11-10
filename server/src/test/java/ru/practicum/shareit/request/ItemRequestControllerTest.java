package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void create_WithValidData_ShouldReturnRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);

        when(itemRequestService.create(any(ItemRequestDto.class), anyLong())).thenReturn(responseDto);

        ItemRequestDto result = itemRequestController.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRequestService).create(requestDto, 1L);
    }

    @Test
    void getByRequester_ShouldReturnRequests() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);

        when(itemRequestService.getByRequester(anyLong())).thenReturn(List.of(requestDto));

        List<ItemRequestDto> result = itemRequestController.getByRequester(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        verify(itemRequestService).getByRequester(1L);
    }

    @Test
    void getAll_ShouldReturnRequests() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);

        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestDto));

        List<ItemRequestDto> result = itemRequestController.getAll(1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRequestService).getAll(1L, 0, 10);
    }

    @Test
    void getById_ShouldReturnRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);

        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(requestDto);

        ItemRequestDto result = itemRequestController.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRequestService).getById(1L, 1L);
    }
}