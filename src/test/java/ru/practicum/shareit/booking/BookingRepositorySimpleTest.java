package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositorySimpleTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBookerIdOrderByStartDesc_ShouldReturnBookings() {
        User user = createUser("User", "user@email.com");
        Item item = createItem("Item", "Description", true, user.getId());
        Booking booking = createBooking(item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(
                user.getId(), PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        return entityManager.persistAndFlush(item);
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return entityManager.persistAndFlush(booking);
    }
}