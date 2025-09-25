package ru.practicum.ewm.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Обновление комментария")
public class UpdateCommentDto {
    @NotBlank
    @Length(min = 2, max = 10000)
    @Schema(description = "Текст комментария", example = "Лучший комментарий",
            minLength = 2, maxLength = 10000)
    private String text;
}
