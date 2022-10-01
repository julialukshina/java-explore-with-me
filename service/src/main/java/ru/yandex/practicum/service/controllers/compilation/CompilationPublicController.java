package ru.yandex.practicum.service.controllers.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.services.compilations.CompilationPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@Validated
public class CompilationPublicController {
    private final CompilationPublicService service;

    @Autowired
    public CompilationPublicController(CompilationPublicService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return service.getCompilationById(compId);
    }
}
