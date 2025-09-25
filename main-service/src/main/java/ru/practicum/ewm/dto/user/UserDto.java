package ru.practicum.ewm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Сущность пользователя")
public class UserDto {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;
    @Schema(description = "Почта пользователя", example = "ivan@mymail.ru")
    private String email;
}
