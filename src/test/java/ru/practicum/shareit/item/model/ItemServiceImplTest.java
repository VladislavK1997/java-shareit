package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

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
    void addItem_ShouldAddItem() {
        User owner = new User(1L, "Owner", "owner@email.com");
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        ItemDto result = itemService.addItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
    }

    @Test
    void addItem_ShouldThrowWhenUserNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void updateItem_ShouldUpdateItem() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item existingItem = new Item(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals("New Name", existingItem.getName());
        assertEquals("New Description", existingItem.getDescription());
    }

    @Test
    void getItemById_ShouldReturnItem() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Item", "Description", true, 1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
    }

    @Test
    void searchItems_ShouldReturnEmptyListForBlankText() {
        List<ItemDto> result = itemService.searchItems("   ", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ShouldAddComment() {
        User user = new User(1L, "User", "user@email.com");
        Item item = new Item(1L, "Item", "Description", true, 2L);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(new ru.practicum.shareit.booking.Booking()));
        when(commentRepository.save(any())).thenAnswer(invocation -> {
            ru.practicum.shareit.item.Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
    }
}