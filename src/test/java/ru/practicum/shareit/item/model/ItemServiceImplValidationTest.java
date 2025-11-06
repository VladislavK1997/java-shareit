package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplValidationTest {

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
    void addItem_ShouldThrowWhenNameIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("   ");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void addItem_ShouldThrowWhenDescriptionIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("   ");
        itemDto.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void addItem_ShouldThrowWhenAvailableIsNull() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(null);

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void updateItem_ShouldNotThrowWhenPartialUpdate() {
        Item existingItem = new Item(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name"); // Only update name

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        assertDoesNotThrow(() -> itemService.updateItem(1L, updateDto, 1L));

        assertEquals("New Name", existingItem.getName());
        assertEquals("Old Description", existingItem.getDescription()); // Unchanged
        assertTrue(existingItem.getAvailable()); // Unchanged
    }

    @Test
    void updateItem_ShouldHandleEmptyUpdates() {
        Item existingItem = new Item(1L, "Name", "Description", true, 1L);
        ItemDto updateDto = new ItemDto(); // No fields to update

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        assertDoesNotThrow(() -> itemService.updateItem(1L, updateDto, 1L));

        assertEquals("Name", existingItem.getName());
        assertEquals("Description", existingItem.getDescription());
        assertTrue(existingItem.getAvailable());
    }
}