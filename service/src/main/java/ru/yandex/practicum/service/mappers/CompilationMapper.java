package ru.yandex.practicum.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.services.events.EventPublicService;

import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Component
public class CompilationMapper {
    private final EventPublicService service;
    private final Statistics statistics;
    @Lazy
    private final EventShortMapper eventShortMapper;

    @Autowired
    public CompilationMapper(EventPublicService service, Statistics statistics, EventShortMapper eventShortMapper) {
        this.service = service;
        this.statistics = statistics;
        this.eventShortMapper = eventShortMapper;
    }


    public CompilationDto toCompilationDto(Compilation compilation) {
        List<Event> events =  compilation.getEvents().stream()
                .collect(Collectors.toList());
        return new CompilationDto(compilation.getId(),
                events.stream()
                        .map(eventShortMapper::toEventShortDto)
                                .collect(Collectors.toList()),
                // TODO: 05.10.2022 статистика
//                statistics.getListEventShortDtoWithViews(events),
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
