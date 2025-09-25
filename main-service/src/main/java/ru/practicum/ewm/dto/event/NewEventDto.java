package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Новое событие")
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    @Schema(description = "Аннотация события",
            example = "Очень интересное и оригинальное событие",
            minLength = 20, maxLength = 2000)
    private String annotation;
    @NotNull
    @Schema(description = "Id категории", example = "1")
    private Long category;
    @NotBlank
    @Length(min = 20, max = 7000)
    @Schema(description = "Описание события",
            example = "Очень интересное и оригинальное описание",
            minLength = 20, maxLength = 7000)
    private String description;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата события", example = "2000-01-01 00:00:00", type = "string")
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    @Schema(description = "Место проведения события")
    private Location location;
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Платное событие или нет")
    private Boolean paid = false;
    @PositiveOrZero
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Максимальное число участников события")
    private Integer participantLimit = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Нужна ли модерация запросов на участие")
    private Boolean requestModeration = true;
    @NotBlank
    @Length(min = 3, max = 120)
    @Schema(description = "Название события", example = "Мой праздник",
            minLength = 3, maxLength = 120)
    private String title;
}
