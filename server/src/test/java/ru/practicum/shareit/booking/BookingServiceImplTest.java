package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        User booker = createUser(1L, "Booker", "booker@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

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
        User booker = createUser(1L, "Booker", "booker@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", false, owner.getId());

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
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_ShouldThrowWhenUserNotFound() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_ShouldThrowWhenEndBeforeStart() {
        User booker = createUser(1L, "Booker", "booker@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_ShouldThrowWhenEndEqualsStart() {
        User booker = createUser(1L, "Booker", "booker@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        LocalDateTime now = LocalDateTime.now();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now);
        bookingDto.setEnd(now);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void updateStatus_ShouldUpdateToApproved() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User booker = createUser(2L, "Booker", "booker@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateStatus(1L, true, 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateStatus_ShouldUpdateToRejected() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User booker = createUser(2L, "Booker", "booker@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateStatus(1L, false, 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void updateStatus_ShouldThrowWhenUserNotOwner() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User otherUser = createUser(2L, "Other", "other@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, createUser(3L, "Booker", "booker@email.com"));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.updateStatus(1L, true, 2L));
    }

    @Test
    void updateStatus_ShouldThrowWhenStatusNotWaiting() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, createUser(2L, "Booker", "booker@email.com"));
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, true, 1L));
    }

    @Test
    void getById_ShouldReturnBooking() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User booker = createUser(2L, "Booker", "booker@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_ShouldThrowWhenUserNotBookerOrOwner() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User booker = createUser(2L, "Booker", "booker@email.com");
        User otherUser = createUser(3L, "Other", "other@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Booking booking = createBooking(1L, item, booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 3L));
    }

    @Test
    void getById_ShouldThrowWhenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(Long id, String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return item;
    }

    private Booking createBooking(Long id, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}