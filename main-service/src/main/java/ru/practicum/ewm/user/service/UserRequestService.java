package ru.practicum.ewm.user.service;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.model.Request;

import java.util.List;

public interface UserRequestService {
    List<RequestDto> getRequests(Long userId);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    Request validateRequest(Long requestId);
}
