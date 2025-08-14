package ru.practicum.ewm.user.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.guest.service.GuestCategoryService;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserEventServiceImpl implements UserEventService {
    private final EventRepository eventRepository;
    private final GuestEventService guestEventService;
    private final GuestCategoryService categoryService;
    private final UserService userService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        QEvent event = QEvent.event;
        JPAQuery<Event> jpaQuery = queryFactory
                .selectFrom(event)
                .where(event.initiator.id.eq(userId))
                .offset(from)
                .limit(size);
        List<Event> events = jpaQuery.fetch();

        return events.stream()
                .map(event1 -> EventMapper.mapToEventShortDto(event1, 0L, 0L))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userService.validateUser(userId);
        Category category = categoryService.validateCategory(eventDto.getCategory());
        Event.validateEventDate(eventDto.getEventDate());
        Event event = EventMapper.mapToEvent(eventDto, user, category);

        return EventMapper.mapToEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = guestEventService.validateEvent(eventId);

        return EventMapper.mapToEventFullDto(event, 0L, 0L);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto) {
        Event event = guestEventService.validateEvent(eventId);
        Long categoryId = eventDto.getCategory();

        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("Некорректный запрос", "Нельзя редактировать опубликованное событие");
        }
        if (categoryId != null) {
            Category category = categoryService.validateCategory(categoryId);
            event.setCategory(category);
        }
        switch (eventDto.getStateAction()) {
            case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            case null -> {}
            default -> throw new DataConflictException("Нет прав для данного действия",
                    "Пользователи могут только отправлять и снимать события с ревью");
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
            Event.validateEventDate(eventDate);
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
