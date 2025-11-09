package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        User owner = createUser(1L, "Owner", "owner@email.com");
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
    void addItem_ShouldThrowWhenNameIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, 1L));
    }

    @Test
    void addItem_ShouldThrowWhenDescriptionIsBlank() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("");
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
    void updateItem_ShouldUpdateItem() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item existingItem = createItem(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");
        updateDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals("New Name", existingItem.getName());
        assertEquals("New Description", existingItem.getDescription());
        assertFalse(existingItem.getAvailable());
    }

    @Test
    void updateItem_ShouldThrowWhenUserNotOwner() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User otherUser = createUser(2L, "Other", "other@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.existsById(2L)).thenReturn(true);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated");

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, updateDto, 2L));
    }

    @Test
    void getItemById_ShouldReturnItem() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, 1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());
    }

    @Test
    void getItemById_ShouldThrowWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void searchItems_ShouldReturnEmptyListForBlankText() {
        var result = itemService.searchItems("   ", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_ShouldReturnEmptyListForNullText() {
        var result = itemService.searchItems(null, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ShouldAddComment() {
        User user = createUser(1L, "User", "user@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(createBooking(user, item)));
        when(commentRepository.save(any())).thenAnswer(invocation -> {
            ru.practicum.shareit.item.Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great item!", result.getText());
    }

    @Test
    void addComment_ShouldThrowWhenUserNotBookedItem() {
        User user = createUser(1L, "User", "user@email.com");
        User owner = createUser(2L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void addComment_ShouldThrowWhenUserNotFound() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void addComment_ShouldThrowWhenItemNotFound() {
        User user = createUser(1L, "User", "user@email.com");
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItems() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item1 = createItem(1L, "Item1", "Description1", true, 1L);
        Item item2 = createItem(2L, "Item2", "Description2", true, 1L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderById(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findLastBookingForItem(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookingForItem(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        var result = itemService.getAllItemsByOwner(1L, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllItemsByOwner_ShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getAllItemsByOwner(1L, 0, 10));
    }

    @Test
    void getItemById_ForNonOwner_ShouldNotIncludeBookingInfo() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User otherUser = createUser(2L, "Other", "other@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getItemById(1L, 2L);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void addBookingInfo_ShouldSetLastAndNextBooking() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingForItem(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(createBooking(owner, item)));
        when(bookingRepository.findNextBookingForItem(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(createBooking(owner, item)));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void addCommentsInfo_ShouldSetComments() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        User author = createUser(2L, "Author", "author@email.com");
        Item item = createItem(1L, "Item", "Description", true, owner.getId());
        Comment comment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1L))
                .thenReturn(Collections.singletonList(comment));

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Great item!", result.getComments().get(0).getText());
    }

    @Test
    void searchItems_WithSpecialCharacters_ShouldReturnResults() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item item = createItem(1L, "Drill-2000", "Powerful electric drill", true, owner.getId());

        when(itemRepository.searchAvailableItems("drill")).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.searchItems("drill", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateItem_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item existingItem = createItem(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals("New Name", existingItem.getName());
        assertEquals("Old Description", existingItem.getDescription());
        assertTrue(existingItem.getAvailable());
    }

    @Test
    void updateItem_WithOnlyDescription_ShouldUpdateOnlyDescription() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item existingItem = createItem(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setDescription("New Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals("Old Name", existingItem.getName());
        assertEquals("New Description", existingItem.getDescription());
        assertTrue(existingItem.getAvailable());
    }

    @Test
    void updateItem_WithOnlyAvailable_ShouldUpdateOnlyAvailable() {
        User owner = createUser(1L, "Owner", "owner@email.com");
        Item existingItem = createItem(1L, "Old Name", "Old Description", true, 1L);
        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals("Old Name", existingItem.getName());
        assertEquals("Old Description", existingItem.getDescription());
        assertFalse(existingItem.getAvailable());
    }

    @Test
    void searchItems_WithEmptyResult_ShouldReturnEmptyList() {
        when(itemRepository.searchAvailableItems("nonexistent")).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems("nonexistent", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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

    private ru.practicum.shareit.booking.Booking createBooking(User booker, Item item) {
        ru.practicum.shareit.booking.Booking booking = new ru.practicum.shareit.booking.Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}