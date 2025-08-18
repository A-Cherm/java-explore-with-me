package ru.practicum.ewm.admin.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.guest.service.GuestCategoryService;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final GuestEventService guestEventService;
    private final GuestCategoryService categoryService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
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
                .rightJoin(request.event, event);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (rangeStart != null) {
                startDate = LocalDateTime.parse(UriUtils.decode(rangeStart, StandardCharsets.UTF_8), formatter);
            }
            if (rangeEnd != null) {
                endDate = LocalDateTime.parse(UriUtils.decode(rangeEnd, StandardCharsets.UTF_8), formatter);
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Некорректный запрос",
                    "Некорректный формат дат: " + rangeStart + ", " + rangeEnd);
        }

        if (users != null) {
            jpaQuery.where(event.initiator.id.in(users));
        }
        if (states != null) {
            jpaQuery.where(event.state.in(states));
        }
        if (categories != null) {
            jpaQuery.where(event.category.id.in(categories));
        }
        if (startDate != null) {
            jpaQuery.where(event.eventDate.after(startDate));
        }
        if (endDate != null) {
            jpaQuery.where(event.eventDate.before(endDate));
        }
        jpaQuery.groupBy(event).offset(from).limit(size);

        List<EventConfirmed> events = jpaQuery.fetch();
        List<Long> ids = events.stream()
                .map(eventConfirmed -> eventConfirmed.getEvent().getId())
                .toList();
        Map<Long, Long> views = guestEventService.getViewsForEvents(ids);

        return events.stream()
                .map(event1 -> EventMapper.mapToEventFullDto(event1.getEvent(),
                        event1.getConfirmed(), views.get(event1.getEvent().getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventDto eventDto) {
        Event event = guestEventService.validateEvent(eventId);
        Long categoryId = eventDto.getCategory();
        LocalDateTime eventDate = eventDto.getEventDate();

        if (categoryId != null) {
            Category category = categoryService.validateCategory(categoryId);
            event.setCategory(category);
        }
        switch (eventDto.getStateAction()) {
            case PUBLISH_EVENT -> {
                if (event.getState() == EventState.PENDING) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new DataConflictException("Некорректные условия операции",
                            "Нельзя опубликовать событие в статусе " + event.getState());
                }
            }
            case REJECT_EVENT -> {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new DataConflictException("Некорректные условия операции",
                            "Нельзя отменить опубликованное событие");
                } else {
                    event.setState(EventState.CANCELED);
                }
            }
            case null -> {
            }
            default -> throw new DataConflictException("Нет прав для данного действия",
                    "Администраторы могут только публиковать и отменять события");
        }
        if (eventDate != null) {
            if (event.getPublishedOn() != null
                    && eventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new DataConflictException("Некорректные условия операции",
                        "Дата события не может быть раньше часа после публикации");
            }
            event.setEventDate(eventDate);
        }
        Event.validateAndUpdateEvent(event, eventDto);
        Event newEvent = eventRepository.save(event);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = guestEventService.getViewsForEvent(eventId);

        return EventMapper.mapToEventFullDto(newEvent, confirmed, views);
    }
}
