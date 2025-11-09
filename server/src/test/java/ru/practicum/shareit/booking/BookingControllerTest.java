package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void create_WithValidData_ShouldReturnBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.create(any(BookingDto.class), anyLong())).thenReturn(responseDto);

        BookingResponseDto result = bookingController.create(bookingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingService).create(bookingDto, 1L);
    }

    @Test
    void getById_WithValidId_ShouldReturnBooking() {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(responseDto);

        BookingResponseDto result = bookingController.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingService).getById(1L, 1L);
    }

    @Test
    void updateStatus_WithApproved_ShouldReturnUpdatedBooking() {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.updateStatus(anyLong(), eq(true), anyLong())).thenReturn(responseDto);

        BookingResponseDto result = bookingController.updateStatus(1L, true, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingService).updateStatus(1L, true, 1L);
    }

    @Test
    void getBookingsByBooker_WithValidState_ShouldReturnBookings() {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.getBookingsByBooker(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        List<BookingResponseDto> result = bookingController.getBookingsByBooker("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        verify(bookingService).getBookingsByBooker(BookingState.ALL, 1L, 0, 10);
    }

    @Test
    void getBookingsByOwner_WithValidState_ShouldReturnBookings() {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.getBookingsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        List<BookingResponseDto> result = bookingController.getBookingsByOwner("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        verify(bookingService).getBookingsByOwner(BookingState.ALL, 1L, 0, 10);
    }
}