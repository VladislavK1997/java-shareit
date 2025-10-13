package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return items.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new RuntimeException("Item not found with id: " + item.getId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public void deleteById(Long id) {
        items.remove(id);
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }
}