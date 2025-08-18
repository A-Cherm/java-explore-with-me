package ru.practicum.ewm.user.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.dto.request.RequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.RequestStatusUpdateResult;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.guest.service.GuestCategoryService;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserEventServiceImpl implements UserEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final GuestEventService guestEventService;
    private final GuestCategoryService categoryService;
    private final UserService userService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        CaseBuilder caseBuilder = Expressions.cases();
        NumberExpression<Long> confirmed = caseBuilder
                .when(request.status.eq(RequestStatus.CONFIRMED))
                .then(1L)
                .otherwise(0L)
                .sum();
        JPAQuery<EventConfirmed> jpaQuery = queryFactory
                .select(Projections.constructor(EventConfirmed.class, event, confirmed))
                .from(request)
                .rightJoin(request.event, event)
                .where(event.initiator.id.eq(userId))
                .groupBy(event)
                .offset(from)
                .limit(size);
        List<EventConfirmed> events = jpaQuery.fetch();
        List<Long> ids = events.stream()
                .map(eventConfirmed -> eventConfirmed.getEvent().getId())
                .toList();
        Map<Long, Long> views = guestEventService.getViewsForEvents(ids);

        return events.stream()
                .map(event1 -> EventMapper.mapToEventShortDto(event1.getEvent(),
                        event1.getConfirmed(), views.get(event1.getEvent().getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userService.validateUser(userId);
        Category category = categoryService.validateCategory(eventDto.getCategory());
        Event.validateEventDate(eventDto.getEventDate());
        Event event = EventMapper.mapToEvent(eventDto, user, category);

        return EventMapper.mapToEventFullDto(eventRepository.save(event), 0L, 0L, List.of());
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = guestEventService.validateEvent(eventId);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = guestEventService.getViewsForEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedDesc(eventId);

        return EventMapper.mapToEventFullDto(event, confirmed, views, comments);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto) {
        Event event = guestEventService.validateEvent(eventId);
        Long categoryId = eventDto.getCategory();
        LocalDateTime eventDate = eventDto.getEventDate();

        if (event.getState() == EventState.PUBLISHED) {
            throw new DataConflictException("Некорректный запрос", "Нельзя редактировать опубликованное событие");
        }
        if (categoryId != null) {
            Category category = categoryService.validateCategory(categoryId);
            event.setCategory(category);
        }
        switch (eventDto.getStateAction()) {
            case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            case null -> {
            }
            default -> throw new DataConflictException("Нет прав для данного действия",
                    "Пользователи могут только отправлять и снимать события с ревью");
        }
        if (eventDate != null) {
            Event.validateEventDate(eventDate);
            event.setEventDate(eventDate);
        }
        Event.validateAndUpdateEvent(event, eventDto);
        Event newEvent = eventRepository.save(event);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = guestEventService.getViewsForEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedDesc(eventId);

        return EventMapper.mapToEventFullDto(newEvent, confirmed, views, comments);
    }

    @Override
    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        guestEventService.validateEvent(eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public RequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, RequestStatusUpdateRequest update) {
        Event event = guestEventService.validateEvent(eventId);
        Long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        boolean isLimitReached = false;
        List<Request> requests = requestRepository
                .findAllByIdInAndEventId(new HashSet<>(update.getRequestIds()), eventId);
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        if (update.getStatus() == RequestStatus.CONFIRMED) {
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                return new RequestStatusUpdateResult(List.of(), List.of());
            } else {
                long freeSpots = event.getParticipantLimit() - confirmed;
                if (requests.size() > freeSpots) {
                    throw new DataConflictException("Нарушены условия запроса",
                            "Достигнут предел участников события");
                }
                if (requests.size() == freeSpots) {
                    isLimitReached = true;
                }
            }
        } else if (update.getStatus() != RequestStatus.REJECTED) {
            throw new ValidationException("Некорректный запрос",
                    "Статус должен быть CONFIRMED или REJECTED");
        }
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new DataConflictException("Нарушены условия запроса",
                        "Заявка с id = " + request.getId() + " не в статусе PENDING");
            }
            request.setStatus(update.getStatus());
        }
        List<RequestDto> requestsDto = requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();

        if (update.getStatus() == RequestStatus.CONFIRMED) {
            confirmedRequests = requestsDto;
        } else {
            rejectedRequests = requestsDto;
        }
        requestRepository.saveAll(requests);

        if (isLimitReached) {
            List<Request> requestsToReject = requestRepository
                    .findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
            requestsToReject.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            rejectedRequests = requestsToReject.stream()
                    .map(RequestMapper::mapToRequestDto)
                    .toList();
            requestRepository.saveAll(requestsToReject);
        }
        return new RequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
