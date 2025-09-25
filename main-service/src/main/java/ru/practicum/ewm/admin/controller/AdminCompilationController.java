package ru.practicum.ewm.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.AdminCompilationService;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;

@RestController
@RequestMapping("admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin: подборки событий", description = "Управление подборками событий")
public class AdminCompilationController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание подборки",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Подборка создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        CompilationDto compilation = compilationService.createCompilation(compilationDto);

        log.info("Создана подборка событий {}", compilation);
        return compilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление подборки",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Подборка удалена"),
                    @ApiResponse(responseCode = "404", description = "Нет подборки с данным id", content = @Content)
            })
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        log.info("Удалена подборка событий с id = {}", compId);
    }

    @PatchMapping("/{compId}")
    @Operation(summary = "Обновление подборки",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Подборка обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет подборки с данным id", content = @Content)
            })
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationDto compilationDto) {
        CompilationDto compilation = compilationService.updateCompilation(compId, compilationDto);

        log.info("Обновлена подборка событий с id = {}: {}", compId, compilation);
        return compilation;
    }
}
