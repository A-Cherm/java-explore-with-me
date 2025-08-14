package ru.practicum.ewm.guest.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuestEventServiceImpl implements GuestEventService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size) {
        QEvent event = QEvent.event;
        JPAQuery<Event> jpaQuery = queryFactory.selectFrom(event).where(event.state.eq(EventState.PUBLISHED));
        Long currentParticipants = 50L;
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
        if (onlyAvailable) {
            jpaQuery.where(event.participantLimit.lt(currentParticipants));
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
        jpaQuery.offset(from).limit(size);

        List<Event> events = jpaQuery.fetch();

        return events.stream()
                .map(event1 -> EventMapper.mapToEventShortDto(event1, currentParticipants, 0L))
                .toList();
    }

    @Override
    public EventFullDto getEvent(Long id) {
        Event event = validateEvent(id);

        if (event.getState() == EventState.PUBLISHED) {
            return EventMapper.mapToEventFullDto(event, 0L, 0L);
        } else {
            throw new NotFoundException("Не найдено событие", "Нет события с id = " + id);
        }
    }

    @Override
    public Event validateEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено событие",
                        "Нет события с id = " + id));
    }
}
