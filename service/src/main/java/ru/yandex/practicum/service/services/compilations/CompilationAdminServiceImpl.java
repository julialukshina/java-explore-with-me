package ru.yandex.practicum.service.services.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.compilations.CompilationMapper;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    /**
     * Создание новой подборки
     *
     * @param dto NewCompilationDto
     * @return CompilationDto
     */

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        if(dto.getEvents()==null){
            List<Long> events = new ArrayList<>();
            dto.setEvents(events);
        }else{
            for (Long id :
                    dto.getEvents()) {
                eventValidation(id);
            }
        }

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.save(compilationMapper.toCompilationFromNew(dto)));
        log.info("Категория с id={} создана", compilationDto.getId());
        return compilationDto;
    }

    /**
     * Удаление подборки по id
     *
     * @param compId Long
     */
    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationValidation(compId);
        compilationRepository.deleteById(compId);
        log.info("Категория с id={} удалена", compId);
    }

    /**
     * Удаление события из подборки
     *
     * @param compId  Long
     * @param eventId Long
     */
    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        compilationValidation(compId);
        eventValidation(eventId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if(!events.contains(eventRepository.findById(eventId).get())){
            throw new NotFoundException(String.format("Событие с id = '%s' в подборке не найдено", eventId));
        }
            events.remove(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
        log.info("Событие с id={} удалено из подборки с id={}", eventId, compId);
    }

    /**
     * Добавление события в подборку
     *
     * @param compId  Long
     * @param eventId Long
     */
    @Override
    @Transactional
    public void addEventFromCompilation(Long compId, Long eventId) {
        compilationValidation(compId);
        eventValidation(eventId);
        Compilation compilation = compilationRepository.findById(compId).get();
        Set<Event> events = compilation.getEvents();
        if(events.contains(eventRepository.findById(eventId).get())){
            throw new NotFoundException(String.format("Событие с id = '%s' уже есть в подборке", eventId));
        }
            events.add(eventRepository.findById(eventId).get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
            log.info("Событие с id={} добавлено в подборку с id={}", eventId, compId);
    }

    /**
     * Открепление подборки
     *
     * @param compId Long
     */
    @Override
    @Transactional
    public void unpinCompilation(Long compId) {
        compilationValidation(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        if (!compilation.isPinned()) {
            throw new ValidationException(String.format("Подборка с id = '%s' уже не закреплена", compId));
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Подборка с id={} откреплена", compId);
    }

    /**
     * Закрепление подборки
     *
     * @param compId Long
     */
    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        compilationValidation(compId);
        Compilation compilation = compilationRepository.findById(compId).get();
        if (compilation.isPinned()) {
            throw new ValidationException(String.format("Подборка с id = '%s' уже закреплена", compId));
        }
            compilation.setPinned(true);
            compilationRepository.save(compilation);
        log.info("Подборка с id={} закреплена", compId);
    }

    /**
     * Проверка наличия события в базе по id
     *
     * @param id Long
     */
    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }

    /**
     * Проверка наличия подборки в базе по id
     *
     * @param id Long
     */

    private void compilationValidation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Подборка с id = '%s' не найдена", id));
        }
    }
}
