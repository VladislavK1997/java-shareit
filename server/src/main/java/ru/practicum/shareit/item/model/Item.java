package ru.practicum.shareit.item.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "request_id")
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}