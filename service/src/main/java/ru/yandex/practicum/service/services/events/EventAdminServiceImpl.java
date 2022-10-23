package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.events.AdminUpdateEventRequest;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.StateInvalidException;
import ru.yandex.practicum.service.exeptions.TimeValidationException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.EventStorage;
import ru.yandex.practicum.service.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventStorage storage;
    private final UserRepository userRepository;
    private final StateEnumConverter converter;
    private final Statistics statistics;
    @Lazy
    private final EventFullMapper eventFullMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public EventAdminServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository,
                                 EventStorage storage, UserRepository userRepository, StateEnumConverter converter,
                                 Statistics statistics, EventFullMapper eventFullMapper) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.storage = storage;
        this.userRepository = userRepository;
        this.converter = converter;
        this.statistics = statistics;
        this.eventFullMapper = eventFullMapper;
    }

    /**
     * Выдача списка событий по заданным критериям поиска.
     *
     * @param users      List<Integer>
     * @param states     List<String>
     * @param categories List<Integer>
     * @param rangeStart String
     * @param rangeEnd   String
     * @param from       int
     * @param size       int
     * @return List<EventFullDto>
     */
    @Override
    @Transactional
    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            try {
                start = LocalDateTime.parse(rangeStart, formatter);
            } catch (DateTimeParseException e) {
                throw new TimeValidationException("Передано некорректное значение для параметра поиска start");
            }
        }
        if (rangeEnd != null) {
            try {
                end = LocalDateTime.parse(rangeEnd, formatter);
            } catch (DateTimeParseException e) {
                throw new TimeValidationException("Передано некорректное значение для параметра поиска end");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Events WHERE ");
        if(users != null){
                userValidation(Long.valueOf(users.get(0)));
                StringBuilder builder = new StringBuilder();
                builder.append(users.get(0));
                if (users.size() > 1) {
                    for (int i = 1; i < users.size(); i++) {
                        userValidation(Long.valueOf(users.get(i)));
                        builder.append(", ").append(users.get(i));
                    }
                sb.append("initiator_id IN (").append(builder).append(") ");
            }
        }

        if (categories != null) {
                categoryValidation(Long.valueOf(categories.get(0)));
                StringBuilder builder = new StringBuilder();
                builder.append(categories.get(0));
                if (categories.size() > 1) {
                    for (int i = 1; i < categories.size(); i++) {
                        categoryValidation(Long.valueOf(categories.get(i)));
                        builder.append(", ").append(categories.get(i));
                    }
                }
                sb.append("AND category_id IN (").append(builder).append(") ");
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
        if (states != null) {
                stateValidation(states.get(0));
                StringBuilder builder = new StringBuilder();
                builder.append("'").append(states.get(0)).append("'");
                if (states.size() > 1) {
                    for (int i = 1; i < states.size(); i++) {
                        stateValidation(states.get(i));
                        builder.append("OR state LIKE '").append(states.get(i)).append("'");
                    }
                }
                sb.append("AND state LIKE ").append(builder).append(" ");
        }


        if (sb.toString().contains("WHERE AND")) {
            int i = sb.indexOf("AND");
            sb.delete(i, i + 3);
        }
        String sqlQuery = String.valueOf(sb);

        List<EventFullDto> dtos = statistics.getListEventFullDtoWithViews(storage.getEvents(sqlQuery));
        log.info("Администратору выдан список событий");
        return dtos;
    }

    /**
     * Обновление события администратором
     *
     * @param eventId            Long
     * @param updateEventRequest UpdateEventRequest
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest updateEventRequest) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventRequest.getCategory()).get());
        }

        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
            if (updateEventRequest.getRequestModeration() != null) {
                event.setRequestModeration(updateEventRequest.getRequestModeration());
            }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        EventFullDto dto = statistics.getEventFullDtoWithViews(eventFullMapper.toEventFullDto(eventRepository.save(event)));
        log.info("Событие с id={} обновлено администратором", dto.getId());
        return dto;
    }

    /**
     * Опубликование события
     *
     * @param eventId Long
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Опубликованы могут быть только события,находящиеся в состоянии ожидания публикации");
        }
        if (event.getEventDate().equals(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Обновлено может быть только событие, до наступления которого осталось больше часа");
        }
        event.setState(State.PUBLISHED);
        EventFullDto dto = eventFullMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id={} опубликовано", eventId);
        return dto;
    }

    /**
     * Отклонение события
     *
     * @param eventId Long
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Опубликованные события не могут быть отклонены");
        }
        event.setState(State.CANCELED);
        EventFullDto dto = eventFullMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id={} отклонено", eventId);
        return dto;
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
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id= '%s' не найден", id));
        }
    }

    /**
     * Валидация поля state у события
     *
     * @param s String
     */
    private void stateValidation(String s) {
        State state = converter.convert(s);
        if (state == State.UNSUPPORTED_STATE) {
            throw new StateInvalidException("Такое состояние для события не предусмотренно");
        }
    }
}
