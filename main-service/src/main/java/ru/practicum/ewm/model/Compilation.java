package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean pinned;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    @ToString.Exclude
    private Set<Long> events;
}
