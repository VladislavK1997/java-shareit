package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation failed");

        ErrorHandler.ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Validation failed", response.getError());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("User not found");

        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals("User not found", response.getError());
    }

    @Test
    void handleForbiddenException_ShouldReturnForbidden() {
        ForbiddenException exception = new ForbiddenException("Access denied");

        ErrorHandler.ErrorResponse response = errorHandler.handleForbiddenException(exception);

        assertNotNull(response);
        assertEquals("Access denied", response.getError());
    }

    @Test
    void handleConflictException_ShouldReturnConflict() {
        ConflictException exception = new ConflictException("Duplicate email");

        ErrorHandler.ErrorResponse response = errorHandler.handleConflictException(exception);

        assertNotNull(response);
        assertEquals("Duplicate email", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError("object", "field", "default message");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("default message", response.getError());
    }

    @Test
    void handleMethodArgumentNotValidException_WithNullFieldError_ShouldReturnGenericMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(null);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError()); // или другое сообщение по умолчанию
    }

    @Test
    void handleMethodArgumentNotValidException_WithNullDefaultMessage_ShouldReturnGenericMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError("object", "field", null);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ErrorHandler.ErrorResponse response = errorHandler.handleException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Internal server error"));
    }
}