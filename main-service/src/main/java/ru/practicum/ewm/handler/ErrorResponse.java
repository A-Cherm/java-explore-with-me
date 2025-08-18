package ru.practicum.ewm.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String status;
    private String reason;
    private String message;
    private String timestamp;

    public static ErrorResponse from(ApiError error) {
        return new ErrorResponse(
                error.getStatus().toString(),
                error.getReason(),
                error.getMessage(),
                formatter.format(error.getTimestamp())
        );
    }

    public static ErrorResponse of(HttpStatus status, String reason, Throwable e) {
        return new ErrorResponse(
                status.toString(),
                reason,
                e.getClass().getCanonicalName() + ": " + e.getMessage(),
                formatter.format(LocalDateTime.now())
        );
    }
}
