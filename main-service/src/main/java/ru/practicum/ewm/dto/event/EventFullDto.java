package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Полная сущность события")
public class EventFullDto {
    @Schema(description = "Идентификатор события", example = "1")
    private Long id;
    @Schema(description = "Аннотация события",
            example = "Очень интересное и оригинальное событие")
    private String annotation;
    @Schema(description = "Категория события")
    private CategoryDto category;
    @Schema(description = "Комментарии к событию")
    private List<CommentDto> comments;
    @Schema(description = "Подтверждённые запросы на участие", example = "5")
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата создания события", example = "2000-01-01 00:00:00", type = "string")
    private LocalDateTime createdOn;
    @Schema(description = "Описание события",
            example = "Очень интересное и оригинальное описание")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата проведения события", example = "2000-01-01 00:00:00", type = "string")
    private LocalDateTime eventDate;
    @Schema(description = "Создатель события")
    private UserShortDto initiator;
    @Schema(description = "Место проведения события")
    private Location location;
    @Schema(description = "Платное событие или нет")
    private Boolean paid;
    @Schema(description = "Максимальное число участников события")
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата публикации события", example = "2000-01-01 00:00:00", type = "string")
    private LocalDateTime publishedOn;
    @Schema(description = "Нужна ли модерация запросов на участие")
    private Boolean requestModeration;
    @Schema(description = "Статус события")
    private EventState state;
    @Schema(description = "Название события", example = "Мой праздник")
    private String title;
    @Schema(description = "Число уникальных простмотров события", example = "10")
    private Long views;
}
