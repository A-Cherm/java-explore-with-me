package ru.practicum.ewm.guest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.guest.service.GuestCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Guest: подборки событий", description = "Просмотр подборок событий")
public class GuestCompilationController {
    private final GuestCompilationService compilationService;

    @GetMapping
    @Operation(summary = "Получение списка подборок событий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);

        log.info("Возвращается список подборок событий: {}", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    @Operation(summary = "Получение подборки событий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Нет подборки с данным id", content = @Content)
            })
    public CompilationDto getCompilation(@PathVariable Long compId) {
        CompilationDto compilation = compilationService.getCompilation(compId);

        log.info("Возвращается подборка событий: {}", compilation);
        return compilation;
    }
}
