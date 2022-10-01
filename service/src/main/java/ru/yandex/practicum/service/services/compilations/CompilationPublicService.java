package ru.yandex.practicum.service.services.compilations;

import ru.yandex.practicum.service.dto.compilations.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);
}
