package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void shouldCreateNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found");
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void shouldCreateValidationException() {
        ValidationException exception = new ValidationException("Validation error");
        assertEquals("Validation error", exception.getMessage());
    }

    @Test
    void shouldCreateForbiddenException() {
        ForbiddenException exception = new ForbiddenException("Forbidden");
        assertEquals("Forbidden", exception.getMessage());
    }

    @Test
    void shouldCreateConflictException() {
        ConflictException exception = new ConflictException("Conflict");
        assertEquals("Conflict", exception.getMessage());
    }
}