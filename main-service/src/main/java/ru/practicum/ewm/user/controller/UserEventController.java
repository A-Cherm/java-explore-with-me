package ru.practicum.ewm.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.dto.request.RequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.RequestStatusUpdateResult;
import ru.practicum.ewm.user.service.UserEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "User: события", description = "Просмотр, создание и обновление событий. " +
        "Просмотр и обновление статуса заявок на участие в событиях пользователя.")
public class UserEventController {
    private final UserEventService eventService;

    @GetMapping
    @Operation(summary = "Получение списка событий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public List<EventShortDto> getEvents(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<EventShortDto> events = eventService.getEvents(userId, from, size);

        log.info("Возвращаются события: {}", events);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание события",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Событие создано"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Поле eventDate должно содержать дату " +
                            "не раньше двух часов от текущей", content = @Content)
            })
    public EventFullDto createEvent(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        EventFullDto event = eventService.createEvent(userId, eventDto);

        log.info("Создано событие: {}", event);
        return event;
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получение события",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public EventFullDto getEvent(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                 @PathVariable @Parameter(description = "Id события") Long eventId) {
        EventFullDto event = eventService.getEvent(userId, eventId);

        log.info("Возвращается событие: {}", event);
        return event;
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Обновление события",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Событие обновлено"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Поле eventDate должно содержать дату " +
                            "не раньше двух часов от текущей", content = @Content)
            })
    public EventFullDto updateEvent(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @PathVariable @Parameter(description = "Id события") Long eventId,
                                    @Valid @RequestBody UpdateEventDto eventDto) {
        EventFullDto event = eventService.updateEvent(userId, eventId, eventDto);

        log.info("Обновлено событие: {}", event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    @Operation(summary = "Получение заявок на участие в событии",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК"),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public List<RequestDto> getEventRequests(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                             @PathVariable Long eventId) {
        List<RequestDto> requests = eventService.getEventRequests(userId, eventId);

        log.info("Возвращаются заявки к событию {}, {}", eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    @Operation(summary = "Изменение статуса у списка заявок",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статусы обновлены"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Нет прав для данного действия. " +
                            "Пользователи могут только отправлять и снимать события с ревью", content = @Content)
            })
    public RequestStatusUpdateResult updateRequestsStatus(
            @PathVariable @Parameter(description = "Id пользователя") Long userId,
            @PathVariable @Parameter(description = "Id события") Long eventId,
            @RequestBody RequestStatusUpdateRequest update
    ) {
        RequestStatusUpdateResult result = eventService.updateRequestsStatus(userId, eventId, update);

        log.info("Обновлён статус событий: {}", result);
        return result;
    }
}
