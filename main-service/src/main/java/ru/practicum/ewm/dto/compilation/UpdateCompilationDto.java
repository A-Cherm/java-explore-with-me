package ru.practicum.ewm.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@AllArgsConstructor
public class UpdateCompilationDto {
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}
