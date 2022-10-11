package ru.yandex.practicum.service.mappers.compilations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.services.events.EventPublicService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Component
public class CompilationMapper {
    private final EventPublicService service;
    @Lazy
    private final EventShortMapper eventShortMapper;

    @Autowired
    public CompilationMapper(EventPublicService service, EventShortMapper eventShortMapper) {
        this.service = service;
        this.eventShortMapper = eventShortMapper;
    }


    public CompilationDto toCompilationDto(Compilation compilation) {
        List<Event> events = new ArrayList<>(compilation.getEvents());
        return new CompilationDto(compilation.getId(),
                events.stream()
                        .map(eventShortMapper::toEventShortDto)
                        .collect(Collectors.toList()),
                compilation.isPinned(),
                compilation.getTitle());
    }

    public Compilation toCompilationFromNew(NewCompilationDto dto) {
        return new Compilation(0,
                dto.getEvents().stream()
                        .map(service::getById)
                        .collect(Collectors.toSet()),
                dto.isPinned(),
                dto.getTitle());
    }
}
