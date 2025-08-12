package ru.practicum.ewm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiError extends RuntimeException {
    private final HttpStatus status;
    private final String reason;
    private final LocalDateTime timestamp;

    public ApiError(HttpStatus status, String reason, String message) {
        super(message);
        this.status = status;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }
}
