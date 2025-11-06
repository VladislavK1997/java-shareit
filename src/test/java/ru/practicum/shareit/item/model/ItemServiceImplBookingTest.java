package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplBookingTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemById_ForOwner_ShouldIncludeBookingInfo() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", true, 1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingForItem(anyLong(), any(), any()))
                .thenReturn(List.of(createBooking(1L, 2L)));
        when(bookingRepository.findNextBookingForItem(anyLong(), any(), any()))
                .thenReturn(List.of(createBooking(3L, 4L)));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(1L, result.getLastBooking().getId());
        assertEquals(3L, result.getNextBooking().getId());
    }

    @Test
    void getItemById_ForNonOwner_ShouldNotIncludeBookingInfo() {
        User nonOwner = new User(2L, "NonOwner", "nonowner@email.com");
        Item item = new Item(1L, "Item", "Description", true, 1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of());

        ItemDto result = itemService.getItemById(1L, 2L);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void addComment_ShouldThrowWhenUserHasNoBookings() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of()); // No bookings found

        assertThrows(ValidationException.class, () ->
                itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void getAllItemsByOwner_ShouldIncludeBookingAndCommentInfo() {
        User owner = new User(1L, "Owner", "owner@email.com");
        User commentAuthor = new User(3L, "CommentAuthor", "author@email.com");
        Item item1 = new Item(1L, "Item1", "Description1", true, 1L);
        Item item2 = new Item(2L, "Item2", "Description2", true, 1L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderById(1L))
                .thenReturn(List.of(item1, item2));
        when(bookingRepository.findLastBookingForItem(anyLong(), any(), any()))
                .thenReturn(List.of(createBooking(1L, 2L)));
        when(bookingRepository.findNextBookingForItem(anyLong(), any(), any()))
                .thenReturn(List.of(createBooking(3L, 4L)));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(createComment("Great!", commentAuthor)));

        List<ItemDto> result = itemService.getAllItemsByOwner(1L, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());

        for (ItemDto itemDto : result) {
            assertNotNull(itemDto.getLastBooking());
            assertNotNull(itemDto.getNextBooking());
            assertNotNull(itemDto.getComments());
            assertFalse(itemDto.getComments().isEmpty());
            assertEquals("CommentAuthor", itemDto.getComments().get(0).getAuthorName());
        }
    }

    @Test
    void searchItems_WithEmptyText() {
        List<ItemDto> result1 = itemService.searchItems("", 0, 10);
        List<ItemDto> result2 = itemService.searchItems("   ", 0, 10);
        List<ItemDto> result3 = itemService.searchItems(null, 0, 10);

        assertNotNull(result1);
        assertTrue(result1.isEmpty());
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    private Booking createBooking(Long bookingId, Long bookerId) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User(bookerId, "Booker", "booker@email.com");
        booking.setBooker(booker);
        return booking;
    }

    private Comment createComment(String text, User author) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        return comment;
    }
}