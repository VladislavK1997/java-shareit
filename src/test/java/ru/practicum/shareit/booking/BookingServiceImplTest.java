package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_ShouldCreateBooking() {
        User booker = new User(1L, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", true, owner.getId());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        BookingResponseDto result = bookingService.create(bookingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_ShouldThrowWhenItemNotAvailable() {
        User booker = new User(1L, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", false, owner.getId());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_ShouldThrowWhenOwnerBooksOwnItem() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", true, owner.getId());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void updateStatus_ShouldApproveBooking() {
        User owner = new User(1L, "Owner", "owner@email.com");
        User booker = new User(2L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Item", "Description", true, owner.getId());
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateStatus(1L, true, 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void getBookingsByBooker_ShouldReturnBookings() {
        User booker = new User(1L, "Booker", "booker@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(BookingStatus.ALL, 1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}