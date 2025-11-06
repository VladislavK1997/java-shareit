package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerExtendedTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("INVALID_STATE");

        var response = errorHandler.handleMethodArgumentTypeMismatchException(exception);

        assertNotNull(response);
        assertEquals("Unknown state: INVALID_STATE", response.getError());
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        var response = errorHandler.handleException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Internal server error"));
    }
}