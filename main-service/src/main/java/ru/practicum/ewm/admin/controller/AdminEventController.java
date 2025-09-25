package ru.practicum.ewm.admin.controller;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.AdminEventService;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.model.EventState;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Admin: события", description = "Просмотр и обновление событий")
public class AdminEventController {
    private final AdminEventService eventService;

    @GetMapping
    @Operation(summary = "Получение списка событий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) @Parameter(description = "Список id пользователей") List<Long> users,
            @RequestParam(required = false) @Parameter(description = "Список статусов событий") List<EventState> states,
            @RequestParam(required = false) @Parameter(description = "Список id категорий") List<Long> categories,
            @RequestParam(required = false) @Parameter(description = "Дата начала интервала времени") String rangeStart,
            @RequestParam(required = false) @Parameter(description = "Дата конца интервала времени") String rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        List<EventFullDto> events = eventService.getEvents(users, states, categories,
                rangeStart, rangeEnd, from, size);

        log.info("Возвращается список событий: {}", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Обновление события",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Событие обновлено"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Нельзя отменить опубликованное событие. " +
                            "Нельзя опубликовать событие не в статусе PENDING.", content = @Content)
            })
    public EventFullDto updateEvent(@PathVariable @Parameter(description = "Id события") Long eventId,
                                    @Valid @RequestBody UpdateEventDto eventDto) {
        EventFullDto updatedEvent = eventService.updateEvent(eventId, eventDto);

        log.info("Обновлено событие: {}", updatedEvent);
        return updatedEvent;
    }
}
