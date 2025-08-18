package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.RequestRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final GuestEventService eventService;

    @Override
    public List<RequestDto> getRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userService.validateUser(userId);
        Event event = eventService.validateEvent(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataConflictException("Невозможно выполнить запрос",
                    "Нельзя создать заявку на неопубликованное событие");
        }
        if (!requestRepository
                .findAllByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new DataConflictException("Невозможно выполнить запрос",
                    "Нельзя создать повторную заявку");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataConflictException("Невозможно выполнить запрос",
                    "Нельзя создать заявку на своё событие");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository
                .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)) {
            throw new DataConflictException("Невозможно выполнить запрос",
                    "Достигнут предел участников события");
        }
        Request request = RequestMapper.mapToRequest(user, event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        Request createdRequest = requestRepository.save(request);

        return RequestMapper.mapToRequestDto(createdRequest);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = validateRequest(requestId);

        requestRepository.deleteById(requestId);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public Request validateRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найдена заявка",
                        "Нет заявки с id = " + requestId));
    }
}
