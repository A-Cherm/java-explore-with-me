package ru.practicum.ewm.guest.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.guest.service.GuestEventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class GuestEventController {
    private final GuestEventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<EventShortDto> events = eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        log.info("Возвращается список событий: {}", events);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id) {
        EventFullDto event = eventService.getEvent(id);

        log.info("Возвращается событие: {}", event);
        return event;
    }
}
