package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Обновление события")
public class UpdateEventDto {
    @Length(min = 20, max = 2000)
    @Schema(description = "Аннотация события",
            example = "Очень интересное и оригинальное событие",
            minLength = 20, maxLength = 2000)
    private String annotation;
    @Schema(description = "Id категории", example = "1")
    private Long category;
    @Length(min = 20, max = 7000)
    @Schema(description = "Описание события",
            example = "Очень интересное и оригинальное описание",
            minLength = 20, maxLength = 7000)
    private String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата события", example = "2000-01-01 10:00:00", type = "string")
    private LocalDateTime eventDate;
    @Schema(description = "Место проведения события")
    private Location location;
    @Schema(description = "Платное событие или нет")
    private Boolean paid;
    @PositiveOrZero
    @Schema(description = "Максимальное число участников события")
    private Integer participantLimit;
    @Schema(description = "Нужна ли модерация запросов на участие")
    private Boolean requestModeration;
    @Schema(description = "Изменение статуса события")
    private EventStateAction stateAction;
    @Length(min = 3, max = 120)
    @Schema(description = "Название события", example = "Мой праздник",
            minLength = 3, maxLength = 120)
    private String title;
}
