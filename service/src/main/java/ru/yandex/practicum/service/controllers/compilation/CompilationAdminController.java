package ru.yandex.practicum.service.controllers.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.services.compilations.CompilationAdminService;

@RestController
@Slf4j
@Validated
public class CompilationAdminController {
    private final CompilationAdminService service;

    @Autowired
    public CompilationAdminController(CompilationAdminService service) {
        this.service = service;
    }


    @PostMapping("/admin/compilations")
    public CompilationDto createCompilation(@RequestBody NewCompilationDto dto) {
        return service.createCompilation(dto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        service.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        service.addEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void unpinCompilation(@PathVariable Long compId) {
        service.unpinCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        service.pinCompilation(compId);
    }
}
