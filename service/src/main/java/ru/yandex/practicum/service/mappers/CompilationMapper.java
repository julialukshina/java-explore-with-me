package ru.yandex.practicum.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.services.events.EventPublicService;

import java.util.stream.Collectors;

@Lazy
@Component
public class CompilationMapper {
    private final EventPublicService service;

    @Autowired
    public CompilationMapper(EventPublicService service) {
        this.service = service;
    }

    // TODO: 22.09.2022 написать мапер после того, как будет готов сервис ивентов

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEvents().stream()
                        .map(event -> EventShortMapper.toEventShortDto(event))
                        .collect(Collectors.toList()),
                compilation.isPinned(),
                compilation.getTitle());
    }

    public Compilation toCompilation(CompilationDto dto) {
        return new Compilation(dto.getId(),
                dto.getEvents().stream()
                        .map(eventShortDto -> service.getById(eventShortDto.getId()))
                        .collect(Collectors.toSet()),
                dto.isPinned(),
                dto.getTitle());
    }

    public Compilation toCompilationFromNew(NewCompilationDto dto) {
        return new Compilation(0,
                dto.getEvents().stream()
                        .map(id -> service.getById(id))
                        .collect(Collectors.toSet()),
                dto.isPinned(),
                dto.getTitle());
    }
}
