package ru.practicum.ewm.admin.service;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.model.EventState;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                 String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventDto eventDto);
}
