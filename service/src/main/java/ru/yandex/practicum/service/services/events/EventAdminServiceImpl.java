package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;
import ru.yandex.practicum.service.exeptions.StateInvalidException;
import ru.yandex.practicum.service.exeptions.TimeValidationException;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.EventStorage;
import ru.yandex.practicum.service.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


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

    // TODO: 27.09.2022 прописать метод
    @Override
    @Transactional
    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            try {
                start = LocalDateTime.parse(rangeStart, formatter);
            } catch (TimeValidationException e) {
                e.getMessage();
            }
        }
        if (rangeEnd != null) {
            try {
                end = LocalDateTime.parse(rangeEnd, formatter);
            } catch (TimeValidationException e) {
                e.getMessage();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Events WHERE ");
        if (users.size() > 0) {
            userValidation(Long.valueOf(users.get(0)));
            StringBuilder builder = new StringBuilder();
            builder.append(categories.get(0));
            if (users.size() > 1) {
                for (int i = 1; i < categories.size(); i++) {
                    userValidation(Long.valueOf(users.get(i)));
                    builder.append(", " + users.get(i));
                }
            }
            sb.append("initiator_id IN (" + builder + ") ");
        }

        if (categories.size() > 0) {
            categoryValidation(Long.valueOf(categories.get(0)));
            StringBuilder builder = new StringBuilder();
            builder.append(categories.get(0));
            if (categories.size() > 1) {
                for (int i = 1; i < categories.size(); i++) {
                    categoryValidation(Long.valueOf(categories.get(i)));
                    builder.append(", " + categories.get(i));
                }
            }
            sb.append("AND category_id IN (" + builder + ") ");
        }

        if (start != null || (start == null && end == null)) {
            if (start == null) {
                start = LocalDateTime.now();
            }
            sb.append(String.format("AND event_date>='%s' ", start));
        }
        if (end != null) {
            sb.append(String.format("AND event_date<='%s' ", end));
        }
        if (states.size() > 0) {
            stateValidation(states.get(0));
            StringBuilder builder = new StringBuilder();
            builder.append("'" + states.get(0) + "'");
            if (states.size() > 1) {
                for (int i = 1; i < states.size(); i++) {
                    stateValidation(states.get(i));
                    builder.append("OR state LIKE '" + states.get(i) + "'");
                }
            }
            sb.append("AND state LIKE " + builder + " ");
        }

        if (sb.toString().contains("WHERE AND")) {
            int i = sb.indexOf("AND");
            sb.delete(i, i + 3);
        }
        String sqlQuery = String.valueOf(sb);

        return statistics.getListEventFullDtoWithViews(storage.getEvents(sqlQuery));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != 0) {
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

            if (updateEventRequest.getRequestModeration() != null) {
                event.setRequestModeration(updateEventRequest.getRequestModeration());
            }
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
//        System.out.println('\n');
//        System.out.println('\n');
//        System.out.println(event.getAnnotation());
//        System.out.println('\n');
//        System.out.println('\n');

        return statistics.getEventFullDtoWithViews(eventFullMapper.toEventFullDto(eventRepository.save(event)));
//       return eventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (!event.getState().equals("PENDING")) {
            throw new MyValidationException("Опубликованы могут быть только события,находящиеся в состоянии ожидания публикации");
        }
        if (event.getEventDate().equals(LocalDateTime.now().plusHours(1))) {
            throw new MyValidationException("Обновлено может быть только событие, до наступления которого осталось больше часа");
        }
        event.setState(State.PUBLISHED);

        // TODO: 02.10.2022 нужно ли здесь добавлять просмотры?

        return eventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if (event.getState().equals("PUBLISHED")) {
            throw new MyValidationException("Опубликованные события не могут быть отклонены");
        }
//        event.setState(State.REJECTED);
        event.setState(State.CANCELED);
        return eventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }

    private void categoryValidation(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new MyNotFoundException(String.format("Категория с id= '%s' не найдена", id));
        }
    }

    private void userValidation(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new MyNotFoundException(String.format("Пользователь с id= '%s' не найден", id));
        }
    }

    private void stateValidation(String s) {
        State state = converter.convert(s);
        if (state == State.UNSUPPORTED_STATE) {
            throw new StateInvalidException("Такое состояние для события не предусмотренно");
        }
    }
}
