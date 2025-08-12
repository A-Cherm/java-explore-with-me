package ru.practicum.ewm.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequest(final ValidationException e) {
        return ErrorResponse.from(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return ErrorResponse.from(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "Некорректный тип параметров запроса", e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolation(final DataIntegrityViolationException e) {
        return ErrorResponse.of(HttpStatus.CONFLICT, "Нарушение ограничений данных", e);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptions(final Throwable e) {
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка", e);
    }
}
