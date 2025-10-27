package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        User owner = getUserById(ownerId);
        validateItem(itemDto);

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = getItemById(itemId);
        getUserById(ownerId);

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User with id " + ownerId + " is not the owner of item " + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        // Добавляем информацию о бронированиях только для владельца
        if (item.getOwnerId().equals(userId)) {
            addBookingInfo(itemDto);
        }

        // Добавляем комментарии для всех пользователей
        addCommentsInfo(itemDto);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId, int from, int size) {
        getUserById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        return items.stream().map(item -> {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            addBookingInfo(itemDto);
            addCommentsInfo(itemDto);
            return itemDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        User author = getUserById(userId);
        Item item = getItemById(itemId);

        // Проверяем, что пользователь действительно брал вещь в аренду
        boolean hasBookings = !bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty();

        if (!hasBookings) {
            throw new ValidationException("User can only comment on items they have booked in the past");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private void addBookingInfo(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();

        // Последнее бронирование
        List<BookingShortDto> lastBookings = bookingRepository
                .findLastBookingForItem(itemDto.getId(), now, PageRequest.of(0, 1))
                .stream().map(booking -> {
                    BookingShortDto dto = new BookingShortDto();
                    dto.setId(booking.getId());
                    dto.setBookerId(booking.getBooker().getId());
                    return dto;
                }).collect(Collectors.toList());

        if (!lastBookings.isEmpty()) {
            itemDto.setLastBooking(lastBookings.get(0));
        }

        // Следующее бронирование
        List<BookingShortDto> nextBookings = bookingRepository
                .findNextBookingForItem(itemDto.getId(), now, PageRequest.of(0, 1))
                .stream().map(booking -> {
                    BookingShortDto dto = new BookingShortDto();
                    dto.setId(booking.getId());
                    dto.setBookerId(booking.getBooker().getId());
                    return dto;
                }).collect(Collectors.toList());

        if (!nextBookings.isEmpty()) {
            itemDto.setNextBooking(nextBookings.get(0));
        }
    }

    private void addCommentsInfo(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available field cannot be null");
        }
    }
}