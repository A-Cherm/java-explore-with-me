package ru.practicum.ewm.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.user.service.UserRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User: заявки", description = "Просмотр, создание и удаление заявок")
public class UserRequestsController {
    private final UserRequestService requestService;

    @GetMapping
    @Operation(summary = "Получение списка заявок пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public List<RequestDto> getRequests(@PathVariable @Parameter(description = "Id пользователя") Long userId) {
        List<RequestDto> requests = requestService.getRequests(userId);

        log.info("Возвращается список заявок пользователя {}: {}", userId, requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание заявки на участие в событии",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заявка создана"),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public RequestDto createRequest(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @RequestParam @Parameter(description = "Id события") Long eventId) {
        RequestDto request = requestService.createRequest(userId, eventId);

        log.info("Создана заявка {}", request);
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Отмена заявки",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка отменена"),
                    @ApiResponse(responseCode = "404", description = "Нет заявки с данным id", content = @Content)
            })
    public RequestDto cancelRequest(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @PathVariable @Parameter(description = "Id заявки") Long requestId) {
        RequestDto request = requestService.cancelRequest(userId, requestId);

        log.info("Удалена заявка {}", request);
        return request;
    }
}
