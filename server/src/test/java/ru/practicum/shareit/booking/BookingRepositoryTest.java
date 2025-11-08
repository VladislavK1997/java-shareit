package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBookerIdOrderByStartDesc_ShouldReturnBookings() {
        User booker = createUser("Booker", "booker@email.com");
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem("Item", "Description", true, owner);

        createBooking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED
        );

        Pageable pageable = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_ShouldReturnOwnerBookings() {
        User booker = createUser("Booker", "booker@email.com");
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem("Item", "Description", true, owner);
        createBooking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item, booker, BookingStatus.APPROVED
        );

        Pageable pageable = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(owner.getId());
        return entityManager.persistAndFlush(item);
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return entityManager.persistAndFlush(booking);
    }
}