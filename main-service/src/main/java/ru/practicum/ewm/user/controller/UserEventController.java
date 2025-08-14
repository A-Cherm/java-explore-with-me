package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventDto;
import ru.practicum.ewm.user.service.UserEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserEventController {
    private final UserEventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<EventShortDto> events = eventService.getEvents(userId, from, size);

        log.info("Возвращаются события: {}", events);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        EventFullDto event = eventService.createEvent(userId, eventDto);

        log.info("Создано событие: {}", event);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        EventFullDto event = eventService.getEvent(userId, eventId);

        log.info("Возвращается событие: {}", event);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody UpdateEventDto eventDto) {
        EventFullDto event = eventService.updateEvent(userId, eventId, eventDto);

        log.info("Обновлено событие: {}", event);
        return event;
    }
}
