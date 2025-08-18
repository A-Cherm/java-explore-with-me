package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiError {
    public NotFoundException(String reason, String message) {
        super(HttpStatus.NOT_FOUND, reason, message);
    }
}
