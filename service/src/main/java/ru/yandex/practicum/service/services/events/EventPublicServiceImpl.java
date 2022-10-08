package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.TimeValidationException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.EventStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository repository;
    private final EventStorage storage;

    private final CategoryRepository categoryRepository;
    private final Statistics statistics;
    @Lazy
    private final EventFullMapper eventFullMapper;
    @Lazy
    private final EventShortMapper eventShortMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EventPublicServiceImpl(EventRepository repository, EventStorage storage, CategoryRepository categoryRepository, Statistics statistics, EventFullMapper eventFullMapper, EventShortMapper eventShortMapper) {
        this.repository = repository;
        this.storage = storage;
        this.categoryRepository = categoryRepository;
        this.statistics = statistics;
        this.eventFullMapper = eventFullMapper;
        this.eventShortMapper = eventShortMapper;
    }

    /**
     * Выдача списка событий по заданным параметрам поиска
     *
     * @param text        String
     * @param categories  List<Integer>
     * @param paid        Boolean
     * @param rangeStart  String
     * @param rangeEnd    String
     * @param isAvailable boolean
     * @param sort        Sort
     * @param from        int
     * @param size        int
     * @return List<EventShortDto>
     */
    @Override
    @Transactional
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, boolean isAvailable, Sort sort, int from, int size) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            try {
                start = LocalDateTime.parse(rangeStart, formatter);
            } catch (TimeValidationException e) {
                throw new TimeValidationException("Передано некорректное значение для параметра поиска start");
            }
        }
        if (rangeEnd != null) {
            try {
                end = LocalDateTime.parse(rangeEnd, formatter);
            } catch (TimeValidationException e) {
                throw new TimeValidationException("Передано некорректное значение для параметра поиска end");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Events WHERE ");
        if (text != null) {
            sb.append(String.format("(annotation ilike '%%%s%%' or description ilike '%%%s%%') ", text, text));
        }
        if (categories.size() > 0) {
            StringBuilder builder = new StringBuilder();
            categoryValidation(Long.valueOf(categories.get(0)));
            builder.append(categories.get(0));
            if (categories.size() > 1) {
                for (int i = 1; i < categories.size(); i++) {
                    categoryValidation(Long.valueOf(categories.get(i)));
                    builder.append(", ").append(categories.get(i));
                }
            }
            sb.append("AND category_id in (").append(builder).append(") ");
        }
        if (paid != null) {
            sb.append(String.format("AND paid IS %s ", paid));
        }
        if (start != null || end == null) {
            if (start == null) {
                start = LocalDateTime.now();
            }
            sb.append(String.format("AND event_date>='%s' ", start));
        }
        if (end != null) {
            sb.append(String.format("AND event_date<='%s' ", end));
        }

        if (sort.equals(Sort.EVENT_DATE)) {
            sb.append("ORDER BY event_date ");
        }
        if (sb.toString().contains("WHERE AND")) {
            int i = sb.indexOf("AND");
            sb.delete(i, i + 3);
        }
        String sqlQuery = String.valueOf(sb);

        List<Event> events = storage.getEvents(sqlQuery);

        if (!events.isEmpty()) {
            if (sort.equals(Sort.EVENT_DATE) || sort.equals(Sort.NO_SORT)) {
                if (isAvailable) {
                    return statistics.getListEventShortDtoWithViews(events.stream()
                            .map(eventFullMapper::toEventFullDto)
                            .filter(eventFullDto -> eventFullDto.getParticipantLimit() == 0 ||
                                    eventFullDto.getConfirmedRequests() < eventFullDto.getParticipantLimit())
                            .skip(from)
                            .limit(size)
                            .map(eventFullMapper::toEvent)
                            .collect(Collectors.toList()));
                } else {
                    return statistics.getListEventShortDtoWithViews(events.stream()
                            .skip(from)
                            .limit(size)
                            .collect(Collectors.toList()));
                }
            }

            if (sort.equals(Sort.VIEWS)) {
                if (isAvailable) {
                    return statistics.getListEventShortDtoWithViews(events.stream()
                            .map(eventFullMapper::toEventFullDto)
                            .filter(eventFullDto -> eventFullDto.getConfirmedRequests() < eventFullDto.getParticipantLimit() ||
                                    eventFullDto.getParticipantLimit() == 0)
                            .sorted(Comparator.comparing(o -> o.getViews()))
                            .skip(from)
                            .limit(size)
                            .map(eventFullMapper::toEvent)
                            .collect(Collectors.toList()));
                } else {
                    return statistics.getListEventShortDtoWithViews(events).stream()
                            .sorted(Comparator.comparing(o -> o.getViews()))
                            .skip(from)
                            .limit(size)
                            .collect(Collectors.toList());
                }
            }
        }
        log.info("Выдан список событий для незарегистрированного пользователя");
        return events.stream()
                .map(eventShortMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    /**
     * Выдача события по id
     *
     * @param id Long
     * @return EventFullDto
     */
    @Override
    public EventFullDto getEventById(Long id) {
        eventValidation(id);
        System.out.println(repository.findById(id));
        EventFullDto dto = eventFullMapper.toEventFullDto(repository.findById(id).get());
        if (!dto.getState().equals("PUBLISHED")) {
            throw new ValidationException("Только опубликованные события могут быть просмотрены");
        }
        log.info("Выдача события по id={} для незарегистрированного пользователя", id);
        return statistics.getEventFullDtoWithViews(dto);
    }

    /**
     * Проверка наличия события в базе по id
     *
     * @param id Long
     */
    private void eventValidation(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Событие с id= '%s' не найдено", id));
        }
    }

    /**
     * Проверка наличия категории в базе по id
     *
     * @param id Long
     */
    private void categoryValidation(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Категория с id= '%s' не найдена", id));
        }
    }

    /**
     * выдача сущности "Событие" по id. Используется в сервисах
     *
     * @param id Long
     * @return Event
     */
    @Override
    public Event getById(Long id) {
        eventValidation(id);
        return repository.findById(id).get();
    }
}
