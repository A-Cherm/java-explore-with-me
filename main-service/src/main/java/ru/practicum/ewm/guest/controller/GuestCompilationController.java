package ru.practicum.ewm.guest.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.guest.service.GuestCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class GuestCompilationController {
    private final GuestCompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);

        log.info("Возвращается список подборок событий: {}", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        CompilationDto compilation = compilationService.getCompilation(compId);

        log.info("Возвращается подборка событий: {}", compilation);
        return compilation;
    }
}
