package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotBlank
    @Length(max = 50)
    private String title;
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<Long> events = new HashSet<>();
}
