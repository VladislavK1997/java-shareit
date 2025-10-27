package ru.practicum.shareit.item.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        validateUserExists(ownerId);

        validateItem(itemDto);

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Item not found with id: " + itemId);
        }

        validateUserExists(ownerId);

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User with id " + ownerId + " is not the owner of item " + itemId);
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Item name cannot be empty");
            }
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Item description cannot be empty");
            }
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.update(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found with id: " + itemId);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        validateUserExists(ownerId);

        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }
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