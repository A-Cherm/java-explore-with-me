package ru.practicum.ewm.guest.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuestEventServiceImpl implements GuestEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size) {
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
                .where(event.state.eq(EventState.PUBLISHED));
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (rangeStart == null && rangeEnd == null) {
            startDate = LocalDateTime.now();
        }
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
        if (text != null && !text.isBlank()) {
            jpaQuery.where(event.annotation.containsIgnoreCase(text)
                    .or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            jpaQuery.where(event.category.id.in(categories));
        }
        if (paid != null) {
            jpaQuery.where(event.paid.eq(paid));
        }
        if (startDate != null) {
            jpaQuery.where(event.eventDate.after(startDate));
        }
        if (endDate != null) {
            jpaQuery.where(event.eventDate.before(endDate));
        }
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE" -> jpaQuery.orderBy(event.eventDate.asc());
                case "VIEWS" -> {
                }
                default -> throw new ValidationException("Некорректный запрос",
                        "Неизвестный параметр сортировки: " + sort);
            }
        }
        jpaQuery.groupBy(event);
        if (onlyAvailable) {
            jpaQuery.having(event.participantLimit.eq(0)
                    .or(event.participantLimit.lt(confirmed)));
        }
        jpaQuery.offset(from).limit(size);

        List<EventConfirmed> events = jpaQuery.fetch();
        List<Long> ids = events.stream()
                .map(eventConfirmed -> eventConfirmed.getEvent().getId())
                .toList();
        Map<Long, Long> views = getViewsForEvents(ids);
        List<EventShortDto> eventsList = new ArrayList<>(events.stream()
                .map(event1 -> EventMapper.mapToEventShortDto(event1.getEvent(),
                        event1.getConfirmed(), views.get(event1.getEvent().getId())))
                .toList());

        if (sort != null && sort.equals("VIEWS")) {
            eventsList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        return eventsList;
    }

    @Override
    public EventFullDto getEvent(Long id) {
        Event event = validateEvent(id);

        if (event.getState() == EventState.PUBLISHED) {
            long views = getViewsForEvent(id);
            long confirmed = requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
            return EventMapper.mapToEventFullDto(event, confirmed, views);
        } else {
            throw new NotFoundException("Не найдено событие", "Нет события с id = " + id);
        }
    }

    @Override
    public List<EventShortDto> getEventsForCompilation(Set<Long> ids) {
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
                .where(event.id.in(ids))
                .groupBy(event);
        List<EventConfirmed> events = jpaQuery.fetch();
        Map<Long, Long> viewStats = getViewsForEvents(ids.stream().toList());

        return events.stream()
                .map(event1 -> EventMapper.mapToEventShortDto(event1.getEvent(),
                        event1.getConfirmed(), viewStats.get(event1.getEvent().getId())))
                .toList();
    }

    @Override
    public Long getViewsForEvent(Long id) {
        LocalDateTime now = LocalDateTime.now();
        String uri = "/events/" + id;
        List<ViewStatsDto> viewStats = statsClient.getViewStats(now.minusYears(1), now, List.of(uri), true);

        if (viewStats.isEmpty()) {
            return 0L;
        } else {
            return viewStats.getFirst().getHits();
        }
    }

    @Override
    public Map<Long, Long> getViewsForEvents(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        LocalDateTime now = LocalDateTime.now();
        List<String> uris = ids.stream()
                .map(id -> "/events/" + id)
                .toList();
        List<ViewStatsDto> viewStats = statsClient.getViewStats(now.minusYears(1), now, uris, true);
        Map<Long, Long> views = new HashMap<>();
        ids.forEach(id -> views.put(id, 0L));

        for (ViewStatsDto viewStat : viewStats) {
            String[] uriSplit = viewStat.getUri().split("/");
            views.put(Long.parseLong(uriSplit[uriSplit.length - 1]), viewStat.getHits());
        }
        return views;
    }

    @Override
    public Event validateEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие",
                        "Нет события с id = " + id));
    }
}
