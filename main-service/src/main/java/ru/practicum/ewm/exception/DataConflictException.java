package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class DataConflictException extends ApiError {
    public DataConflictException(String reason, String message) {
        super(HttpStatus.CONFLICT, reason, message);
    }
}
