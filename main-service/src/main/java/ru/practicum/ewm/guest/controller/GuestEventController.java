package ru.practicum.ewm.guest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.guest.service.GuestEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class GuestEventController {
    private final GuestEventService eventService;
    private final StatsClient statsClient;
    @Value("${api.url}")
    private String url;
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<@Positive Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        List<EventShortDto> events = eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        saveEndpointHit(request);
        log.info("Возвращается список событий: {}", events);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id,
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
