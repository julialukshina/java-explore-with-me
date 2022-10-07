package ru.yandex.practicum.service.services.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.MyPageable;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.compilations.CompilationMapper;
import ru.yandex.practicum.service.repositories.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository repository;
    @Lazy
    @Autowired
    private CompilationMapper mapper;

    @Autowired
    public CompilationPublicServiceImpl(CompilationRepository repository) {
        this.repository = repository;
    }

    /**
     * Выдача списка подборки. Возможный параметр - закрепленность подборки
     *
     * @param pinned Boolean
     * @param from int
     * @param size int
     * @return List<CompilationDto>
     */
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = MyPageable.of(from, size);
        if (pinned == null) {
            return repository.findAll(pageable).stream()
                    .map(compilation -> mapper.toCompilationDto(compilation))
                    .collect(Collectors.toList());
        }
        List<CompilationDto> dtos = repository.findByPinned(pinned, pageable).stream()
                .map(compilation -> mapper.toCompilationDto(compilation))
                .collect(Collectors.toList());
        log.info("Выдан список подборок");
        return dtos;
    }

    /**
     * Выдача подборки по id
     *
     * @param compId Long
     * @return CompilationDto
     */
    @Override
    public CompilationDto getCompilationById(Long compId) {
        if (!repository.existsById(compId)) {
            throw new MyNotFoundException(String.format("The compilation with id= '%s' is not found", compId));
        }
        CompilationDto dto = mapper.toCompilationDto(repository.findById(compId).get());
        log.info("Подборка с id={} выдана", compId);
        return dto;
    }
}
