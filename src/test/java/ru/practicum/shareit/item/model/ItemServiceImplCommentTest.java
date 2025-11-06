package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplCommentTest {

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
    void addComment_ShouldValidateBookingConditions() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));

        Booking pastBooking = new Booking();
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));

        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(pastBooking));
        when(commentRepository.save(any())).thenAnswer(invocation -> {
            ru.practicum.shareit.item.Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        assertDoesNotThrow(() -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void getItemById_ShouldHandleDifferentUserScenarios() {
        User owner = new User(1L, "Owner", "owner@email.com");
        User otherUser = new User(2L, "Other", "other@email.com");
        Item item = new Item(1L, "Item", "Description", true, 1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(List.of());

        when(bookingRepository.findLastBookingForItem(anyLong(), any(), any())).thenReturn(List.of());
        when(bookingRepository.findNextBookingForItem(anyLong(), any(), any())).thenReturn(List.of());

        var result1 = itemService.getItemById(1L, 1L);
        assertNotNull(result1);

        var result2 = itemService.getItemById(1L, 2L);
        assertNotNull(result2);
        assertNull(result2.getLastBooking());
        assertNull(result2.getNextBooking());
    }
}