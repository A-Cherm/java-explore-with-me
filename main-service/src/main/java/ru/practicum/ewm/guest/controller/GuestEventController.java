package ru.practicum.ewm.guest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.guest.service.GuestEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Guest: события", description = "Просмотр событий")
public class GuestEventController {
    private final GuestEventService eventService;
    private final StatsClient statsClient;
    @Value("${api.url}")
    private String url;
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping
    @Operation(summary = "Получение списка событий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) @Parameter(description = "Поиск по тексту в аннотации и описании") String text,
            @RequestParam(required = false) @Parameter(description = "Список id категорий") List<@Positive Long> categories,
            @RequestParam(required = false) @Parameter(description = "Платное событие или нет")Boolean paid,
            @RequestParam(required = false) @Parameter(description = "Дата начала интервала времени") String rangeStart,
            @RequestParam(required = false) @Parameter(description = "Дата конца интервала времени") String rangeEnd,
            @RequestParam(defaultValue = "false") @Parameter(description = "Есть ли места") Boolean onlyAvailable,
            @RequestParam(required = false) @Parameter(description = "Тип сортировки: EVENT_DATE или VIEWS") String sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        List<EventShortDto> events = eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        saveEndpointHit(request);
        log.info("Возвращается список событий: {}", events);
        return events;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение события",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public EventFullDto getEvent(@PathVariable @Parameter(description = "Id события") Long id,
                                 HttpServletRequest request) {
        EventFullDto event = eventService.getEvent(id);

        saveEndpointHit(request);
        log.info("Возвращается событие: {}", event);
        return event;
    }

    private void saveEndpointHit(HttpServletRequest request) {
        log.info("url: {}", url);
        statsClient.saveEndpointHit(appName, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());
        log.info("Отправлен запрос в сервис статистики: url = {}, ip = {}",
                request.getRequestURI(), request.getRemoteAddr());
    }
}
