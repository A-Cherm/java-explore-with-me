package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWithConfirmed {
    private Long eventId;
    private Long confirmed;
}
