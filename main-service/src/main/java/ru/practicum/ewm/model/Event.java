package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.exception.DataConflictException;

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
}
