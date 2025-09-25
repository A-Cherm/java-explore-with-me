package ru.practicum.ewm.dto.compilation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Сущность подборки событий")
public class CompilationDto {
    @Schema(description = "Идентификатор события", example = "1")
    private Long id;
    @Schema(description = "Название подборки", example = "Необычные экскурсии")
    private String title;
    @Schema(description = "Отображается ли на главной странице", example = "true")
    private Boolean pinned;
    @Schema(description = "Список событий")
    private List<EventShortDto> events;
}
