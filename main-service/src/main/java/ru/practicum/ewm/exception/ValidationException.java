package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApiError {
    public ValidationException(String reason, String message) {
        super(HttpStatus.BAD_REQUEST, reason, message);
    }
}
