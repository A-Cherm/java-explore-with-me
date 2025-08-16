package ru.practicum.ewm.guest.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GuestEventService {
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                  String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                  String sort, Integer from, Integer size);

    EventFullDto getEvent(Long id);

    List<EventShortDto> getEventsForCompilation(Set<Long> ids);

    Long getViewsForEvent(Long id);

    Map<Long, Long> getViewsForEvents(List<Long> ids);

    Event validateEvent(Long id);
}
