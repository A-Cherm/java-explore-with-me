package ru.practicum.ewm.dto.compilation;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Новая подборка событий")
public class NewCompilationDto {
    @NotBlank
    @Length(max = 50)
    @Schema(description = "Название подборки", example = "Мои любимые экскурсии",
            maxLength = 50)
    private String title;
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Отображается ли на главной странице", example = "true")
    private Boolean pinned = false;
    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Список id событий", example = "[1,2,3]")
    private Set<Long> events = new HashSet<>();
}
