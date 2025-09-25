package ru.practicum.ewm.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность комментария")
public class CommentDto {
    @Schema(description = "Идентификатор комментария", example = "1")
    private Long id;
    @Schema(description = "Автор комментария")
    private UserShortDto author;
    @Schema(description = "Текст комментария", example = "Лучший комментарий")
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата создания комментария", example = "2000-01-01 00:00:00", type = "string")
    private LocalDateTime created;
}
