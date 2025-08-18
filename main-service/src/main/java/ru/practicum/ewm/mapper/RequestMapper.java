package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

public class RequestMapper {
    public static RequestDto mapToRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getStatus()
        );
    }

    public static Request mapToRequest(User user, Event event) {
        return new Request(
                null,
                user,
                event,
                LocalDateTime.now(),
                RequestStatus.PENDING
        );
    }
}
