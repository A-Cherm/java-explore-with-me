package ru.practicum.ewm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Краткая сущность пользователя")
public class UserShortDto {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;
}
