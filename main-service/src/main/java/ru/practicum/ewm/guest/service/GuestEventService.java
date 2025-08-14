package ru.practicum.ewm.guest.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Event;

import java.util.List;

public interface GuestEventService {
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                  String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                  String sort, Integer from, Integer size);

    EventFullDto getEvent(Long id);

    Event validateEvent(Long id);
}
