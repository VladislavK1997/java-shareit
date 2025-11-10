package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus from(String stringState) {
        for (BookingStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + stringState);
    }
}