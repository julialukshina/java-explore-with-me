package ru.yandex.practicum.service.services.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.CompilationMapper;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;

import java.util.*;

@Service
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final EventRepository eventRepository;

    private final CompilationRepository compilationRepository;
    @Lazy
    private final CompilationMapper compilationMapper;
    @Autowired
    public CompilationAdminServiceImpl(EventRepository eventRepository,
                                       CompilationRepository compilationRepository,
                                       CompilationMapper compilationMapper) {
        this.eventRepository = eventRepository;
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        for (Long id :
                dto.getEvents()) {
            eventValidation(id);
        }
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.save(compilationMapper.toCompilationFromNew(dto)));

        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationValidation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {

        // TODO: 28.09.2022 пщдумай над этим методом, слишком заморочено

        compilationValidation(compId);
        eventValidation(eventId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if (events.contains(eventRepository.findById(eventId).get())) {
            events.remove(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
        }
    }

    @Override
    public void addEventFromCompilation(Long compId, Long eventId) {
        // TODO: 28.09.2022 пщдумай над этим методом, слишком заморочено

        compilationValidation(compId);
        eventValidation(eventId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if (!events.contains(eventRepository.findById(eventId).get())) {
            events.add(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
        }
    }

    @Override
    public void unpinCompilation(Long compId) {
        compilationValidation(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        if (compilation.isPinned()) {
            compilation.setPinned(false);
            compilationRepository.save(compilation);
        }
    }

    @Override
    public void pinCompilation(Long compId) {
        compilationValidation(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        if (!compilation.isPinned()) {
            compilation.setPinned(true);
            compilationRepository.save(compilation);
        }
    }

    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }


    private void compilationValidation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Подборка с id = '%s' не найдена", id));
        }
    }
}
