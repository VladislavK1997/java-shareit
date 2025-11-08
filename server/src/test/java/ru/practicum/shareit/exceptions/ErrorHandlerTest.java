package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorHandler.ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Not found");

        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals("Not found", response.getError());
    }

    @Test
    void handleForbiddenException_ShouldReturnForbidden() {
        ForbiddenException exception = new ForbiddenException("Forbidden");

        ErrorHandler.ErrorResponse response = errorHandler.handleForbiddenException(exception);

        assertNotNull(response);
        assertEquals("Forbidden", response.getError());
    }

    @Test
    void handleConflictException_ShouldReturnConflict() {
        ConflictException exception = new ConflictException("Conflict");

        ErrorHandler.ErrorResponse response = errorHandler.handleConflictException(exception);

        assertNotNull(response);
        assertEquals("Conflict", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_WithFieldError_ShouldReturnFieldMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldError()).thenReturn(mock(org.springframework.validation.FieldError.class));
        when(exception.getBindingResult().getFieldError().getDefaultMessage()).thenReturn("Field error message");

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Field error message", response.getError());
    }

    @Test
    void handleMissingRequestHeaderException_ShouldReturnBadRequest() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Sharer-User-Id", null);

        ErrorHandler.ErrorResponse response = errorHandler.handleMissingRequestHeaderException(exception);

        assertNotNull(response);
        assertEquals("Missing required header: X-Sharer-User-Id", response.getError());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("INVALID_STATE");

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentTypeMismatchException(exception);

        assertNotNull(response);
        assertEquals("Unknown state: INVALID_STATE", response.getError());
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Test error");

        ErrorHandler.ErrorResponse response = errorHandler.handleException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Internal server error: Test error"));
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() {
        ErrorHandler.ErrorResponse errorResponse = new ErrorHandler.ErrorResponse("Test error");

        assertNotNull(errorResponse);
        assertEquals("Test error", errorResponse.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_WithNullFieldError_ShouldReturnDefaultMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldError()).thenReturn(null);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_WithNullDefaultMessage_ShouldReturnDefaultMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldError()).thenReturn(mock(org.springframework.validation.FieldError.class));
        when(exception.getBindingResult().getFieldError().getDefaultMessage()).thenReturn(null);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }
}