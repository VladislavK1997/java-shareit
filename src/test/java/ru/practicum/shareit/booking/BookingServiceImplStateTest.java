package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplStateTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void getBookingsByBooker_AllStates() {

        User booker = new User(1L, "Booker", "booker@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        setupAllBookerStubs();

        BookingStatus[] states = {
                BookingStatus.ALL,
                BookingStatus.CURRENT,
                BookingStatus.PAST,
                BookingStatus.FUTURE,
                BookingStatus.WAITING,
                BookingStatus.REJECTED
        };

        for (BookingStatus state : states) {
            List<BookingResponseDto> result = assertDoesNotThrow(() ->
                    bookingService.getBookingsByBooker(state, 1L, 0, 10));

            assertNotNull(result);
        }
    }

    @Test
    void getBookingsByOwner_AllStates() {
        User owner = new User(1L, "Owner", "owner@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        setupAllOwnerStubs();

        BookingStatus[] states = {
                BookingStatus.ALL,
                BookingStatus.CURRENT,
                BookingStatus.PAST,
                BookingStatus.FUTURE,
                BookingStatus.WAITING,
                BookingStatus.REJECTED
        };

        for (BookingStatus state : states) {
            List<BookingResponseDto> result = assertDoesNotThrow(() ->
                    bookingService.getBookingsByOwner(state, 1L, 0, 10));

            assertNotNull(result);
        }
    }

    private void setupAllBookerStubs() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
    }

    private void setupAllOwnerStubs() {
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));
    }

    @Test
    void getBookingsByBooker_ShouldThrowForUnknownState() {
        User booker = new User(1L, "Booker", "booker@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsByBooker(BookingStatus.CANCELED, 1L, 0, 10);
        });
    }

    @Test
    void getBookingsByOwner_ShouldThrowForUnknownState() {
        User owner = new User(1L, "Owner", "owner@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsByOwner(BookingStatus.CANCELED, 1L, 0, 10);
        });
    }
}