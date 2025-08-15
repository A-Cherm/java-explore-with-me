package ru.practicum.ewm.user.service;

import ru.practicum.ewm.dto.*;

import java.util.List;

public interface UserEventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    RequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, RequestStatusUpdateRequest update);
}
