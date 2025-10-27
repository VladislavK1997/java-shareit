package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingDto bookingDto, Long userId) {
        User booker = getUserById(userId);
        Item item = getItemById(bookingDto.getItemId());

        // Проверки
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }
        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner cannot book their own item");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Invalid booking dates");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long bookingId, Boolean approved, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Only item owner can update booking status");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Only booker or owner can view booking");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByBooker(BookingStatus state, Long bookerId, int from, int size) {
        getUserById(bookerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                                bookerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                                bookerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                // Для WAITING и REJECTED используем соответствующие статусы бронирования
                BookingStatus bookingStatus = state == BookingStatus.WAITING ?
                        BookingStatus.WAITING : BookingStatus.REJECTED;
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, bookingStatus, pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(BookingStatus state, Long ownerId, int from, int size) {
        getUserById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                                ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                                ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                // Для WAITING и REJECTED используем соответствующие статусы бронирования
                BookingStatus bookingStatus = state == BookingStatus.WAITING ?
                        BookingStatus.WAITING : BookingStatus.REJECTED;
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, bookingStatus, pageable)
                        .stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }
}