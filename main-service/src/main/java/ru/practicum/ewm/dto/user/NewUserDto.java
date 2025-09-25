package ru.practicum.ewm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Schema(description = "Новый пользователь")
public class NewUserDto {
    @NotBlank
    @Length(min = 2, max = 250)
    @Schema(description = "Имя пользователя", example = "Иван",
            minLength = 2, maxLength = 250)
    private String name;
    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    @Schema(description = "Почта пользователя", example = "ivan@mymail.ru",
            minLength = 6, maxLength = 254)
    private String email;
}
