package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplComplexTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_ShouldValidateAllDateScenarios() {
        User booker = new User(1L, "Booker", "booker@email.com");
        User owner = new User(2L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", true, owner.getId());

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingDto dto1 = new BookingDto();
        dto1.setItemId(1L);
        dto1.setStart(LocalDateTime.now().plusDays(2));
        dto1.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(dto1, 1L));

        BookingDto dto2 = new BookingDto();
        dto2.setItemId(1L);
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        dto2.setStart(sameTime);
        dto2.setEnd(sameTime);

        assertThrows(ValidationException.class, () -> bookingService.create(dto2, 1L));

        BookingDto dto3 = new BookingDto();
        dto3.setItemId(1L);
        dto3.setStart(LocalDateTime.now().plusDays(1));
        dto3.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());

        assertDoesNotThrow(() -> bookingService.create(dto3, 1L));
    }

    @Test
    void updateStatus_ShouldHandleAllStatusScenarios() {
        User owner = new User(1L, "Owner", "owner@email.com");
        User booker = new User(2L, "Booker", "booker@email.com");
        Item item = new Item(1L, "Item", "Description", true, owner.getId());

        Booking waitingBooking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBooking);

        assertDoesNotThrow(() -> bookingService.updateStatus(1L, true, 1L));
        assertEquals(BookingStatus.APPROVED, waitingBooking.getStatus());

        Booking waitingBooking2 = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(waitingBooking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBooking2);

        assertDoesNotThrow(() -> bookingService.updateStatus(2L, false, 1L));
        assertEquals(BookingStatus.REJECTED, waitingBooking2.getStatus());

        Booking approvedBooking = new Booking(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findById(3L)).thenReturn(Optional.of(approvedBooking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(3L, true, 1L));
    }
}