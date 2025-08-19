package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class EventMapper {
    public static Event mapToEvent(NewEventDto eventDto, User user, Category category) {
        return new Event(
                null,
                eventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                eventDto.getDescription(),
                eventDto.getEventDate(),
                user,
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                null,
                eventDto.getRequestModeration(),
                EventState.PENDING,
                eventDto.getTitle()
        );
    }

    public static EventFullDto mapToEventFullDto(Event event, Long confirmed,
                                                 Long views, List<Comment> comments) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                comments.stream().map(CommentMapper::mapToCommentDto).toList(),
                confirmed,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                views
        );
    }

    public static EventShortDto mapToEventShortDto(Event event, Long confirmed, Long views) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                confirmed,
                event.getEventDate(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                views
        );
    }
}
