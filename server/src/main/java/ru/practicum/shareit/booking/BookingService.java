package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingDto bookingDto, Long userId);

    BookingResponseDto updateStatus(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getById(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByBooker(BookingState state, Long bookerId, int from, int size);

    List<BookingResponseDto> getBookingsByOwner(BookingState state, Long ownerId, int from, int size);
}

enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}