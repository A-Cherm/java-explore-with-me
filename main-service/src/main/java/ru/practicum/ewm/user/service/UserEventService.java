package ru.practicum.ewm.user.service;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.dto.request.RequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.RequestStatusUpdateResult;

import java.util.List;

public interface UserEventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    RequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, RequestStatusUpdateRequest update);
}
