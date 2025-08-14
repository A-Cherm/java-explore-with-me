package ru.practicum.ewm.admin.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.dto.UpdateEventDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.guest.service.GuestCategoryService;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.QEvent;
import ru.practicum.ewm.repository.EventRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final GuestEventService guestEventService;
    private final GuestCategoryService categoryService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {

        QEvent event = QEvent.event;
        JPAQuery<Event> jpaQuery = queryFactory.selectFrom(event);
        Long currentParticipants = 50L;
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
        jpaQuery.offset(from).limit(size);

        List<Event> events = jpaQuery.fetch();

        return events.stream()
                .map(event1 -> EventMapper.mapToEventFullDto(event1, currentParticipants, 0L))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventDto eventDto) {
        Event event = guestEventService.validateEvent(eventId);
        Long categoryId = eventDto.getCategory();

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
            case null -> {}
            default -> throw new DataConflictException("Нет прав для данного действия",
                    "Администраторы могут только публиковать и отменять события");
        }
        String annotation = eventDto.getAnnotation();
        String description = eventDto.getDescription();
        String title = eventDto.getTitle();
        Location location = eventDto.getLocation();
        LocalDateTime eventDate = eventDto.getEventDate();

        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (location != null) {
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        }
        if (eventDate != null) {
            if (event.getPublishedOn() != null
                    && eventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new DataConflictException("Некорректные условия операции",
                        "Дата события не может быть раньше часа после публикации");
            }
            event.setEventDate(eventDate);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        Event newEvent = eventRepository.save(event);

        return EventMapper.mapToEventFullDto(newEvent, 0L, 0L);
    }
}
