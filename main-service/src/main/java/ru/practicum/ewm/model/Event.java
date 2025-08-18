package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;
    @Column(name = "created")
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;
    @Column(name = "location_lat")
    private float locationLat;
    @Column(name = "location_lon")
    private float locationLon;
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Column(name = "moderation")
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        return id != null && id.equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static void validateEventDate(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();

        if (date.isBefore(now.plusHours(2))) {
            throw new DataConflictException("Нарушены условия создания события",
                    "Поле eventDate должно содержать дату не раньше двух часов от текущей: " + date);
        }
    }

    public static void validateAndUpdateEvent(Event event, UpdateEventDto eventDto) {
        String annotation = eventDto.getAnnotation();
        String description = eventDto.getDescription();
        String title = eventDto.getTitle();
        Location location = eventDto.getLocation();
        LocalDateTime eventDate = eventDto.getEventDate();

        if (annotation != null && annotation.isBlank()) {
            throw new ValidationException("Некорректные данные запроса",
                    "Аннотация не может состоять только из пробелов");
        }
        if (description != null && description.isBlank()) {
            throw new ValidationException("Некорректные данные запроса",
                    "Описание не может состоять только из пробелов");
        }
        if (title != null && title.isBlank()) {
            throw new ValidationException("Некорректные данные запроса",
                    "Заголовок не может состоять только из пробелов");
        }

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (title != null) {
            event.setTitle(title);
        }
        if (location != null) {
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
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
    }
}
