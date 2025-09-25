package ru.practicum.ewm.dto.compilation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@AllArgsConstructor
@Schema(description = "Обновление подборки событий")
public class UpdateCompilationDto {
    @Length(min = 1, max = 50)
    @Schema(description = "Название подборки", example = "Мои любимые экскурсии",
            maxLength = 50)
    private String title;
    @Schema(description = "Отображается ли на главной странице", example = "true")
    private Boolean pinned;
    @Schema(description = "Список id событий", example = "[1,2,3]")
    private Set<Long> events;
}
