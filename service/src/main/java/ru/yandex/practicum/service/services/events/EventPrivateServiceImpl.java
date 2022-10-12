package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Pageable;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.events.NewEventDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.requests.RequestMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Statistics statistics;
    @Lazy
    private final EventFullMapper eventFullMapper;

    @Autowired
    public EventPrivateServiceImpl(UserRepository userRepository, EventRepository eventRepository, CategoryRepository categoryRepository, RequestRepository requestRepository, Statistics statistics, EventFullMapper eventFullMapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statistics = statistics;
        this.eventFullMapper = eventFullMapper;
    }

    /**
     * Выдача списка событий, созданных пользователем
     *
     * @param userId Long
     * @param from   int
     * @param size   int
     * @return List<EventShortDto>
     */
    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, int from, int size) {
        userValidation(userId);
        org.springframework.data.domain.Pageable pageable = Pageable.of(from, size);
        List<EventShortDto> eventShortDtos = statistics.getListEventShortDtoWithViews(eventRepository.findByInitiatorId(userId, pageable).toList());
        log.info("Пользователю с id={} выдан список созданных им событий", userId);
        return eventShortDtos;
    }

    /**
     * Обновление события пользователем
     *
     * @param userId             Long
     * @param updateEventRequest UpdateEventRequest
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        userValidation(userId);
        eventValidation(updateEventRequest.getEventId());
        if (LocalDateTime.parse(updateEventRequest.getEventDate(), formatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Изменение события доступно не менее, чем за два часа до его наступления");
        }

        Event event = eventRepository.findById(updateEventRequest.getEventId()).get();
        initiatorValidation(userId, event.getInitiator().getId());
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Только отмененные или ожидающие публикации события могут быть обновлены");
        }

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
        } else {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Событие не может состояться ранее, чем через два часа после его обновления");
            }
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
        if (event.getState().equals(State.CANCELED)) {
            event.setState(State.PENDING);
        }

        EventFullDto dto = eventFullMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id={} обновлено пользователем с id={}", dto.getId(), userId);
        return dto;
    }

    /**
     * Выдача события пользователю по id
     *
     * @param userId  Long
     * @param eventId Long
     * @return EventFullDto
     */
    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        EventFullDto dto = statistics.getEventFullDtoWithViews(eventFullMapper.toEventFullDto(event));
        log.info("Событие с id={} предоставлено пользователю с id={}", eventId, userId);
        return dto;
    }

    /**
     * Создание события
     *
     * @param userId Long
     * @param dto    NewEventDto
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto postEvent(Long userId, NewEventDto dto) {
        userValidation(userId);
        LocalDateTime now = LocalDateTime.now();
        if (LocalDateTime.parse(dto.getEventDate(), formatter).isBefore(now.plusHours(2))) {
            throw new ValidationException("Событие может быть опубликовано не менее, чем за два часа до его наступления");
        }
        Event event = new Event(0,
                dto.getAnnotation(),
                categoryRepository.findById(dto.getCategory()).get(),
                now,
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate(), formatter),
                userRepository.findById(userId).get(),
                dto.isPaid(),
                dto.getParticipantLimit(),
                null,
                dto.isRequestModeration(),
                dto.getTitle(),
                State.PENDING,
                dto.isCommentModeration());
        EventFullDto eventFullDto = eventFullMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id={} создано пользователем с id={}", eventFullDto.getId(), userId);
        return eventFullDto;
    }

    /**
     * Отмена события пользователем
     *
     * @param userId  Long
     * @param eventId Long
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Только ожидающее модерации событие может быть отменено");
        }
        event.setState(State.CANCELED);
        EventFullDto dto = eventFullMapper.toEventFullDto(eventRepository.save(event));
        log.info("Событие с id={} отменено пользователем с id={}", eventId, userId);
        return dto;
    }

    /**
     * Выдача запросов на участие в событии, созданном пользователем
     *
     * @param userId  Long
     * @param eventId Long
     * @return List<ParticipationRequestDto>
     */
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        List<ParticipationRequestDto> dtos = requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("Запросы на участие в событии с id={} выдано пользователю d={}", eventId, userId);
        return dtos;
    }

    /**
     * Подтверждение запроса на участие в событии
     *
     * @param userId  Long
     * @param eventId Long
     * @param reqId   Long
     * @return ParticipationRequestDto
     */
    @Override
    @Transactional
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        userValidation(userId);
        eventValidation(eventId);
        initiatorValidationWithEventId(userId, eventId);
        requestValidation(reqId);
        eventAndInitiatorRequestValidation(reqId, eventId, userId);
        Request request = requestRepository.findById(reqId).get();
        request.setStatus(Status.CONFIRMED);
        ParticipationRequestDto dto = RequestMapper.toRequestDto(requestRepository.save(request));
        EventFullDto eventFullDto = eventFullMapper.toEventFullDto(eventRepository.findById(eventId).get());
        if (eventFullDto.getParticipantLimit() != 0 && eventFullDto.getConfirmedRequests() == eventFullDto.getParticipantLimit()) {
            if (requestRepository.existsByEventIdAndStatus(eventId, Status.PENDING)) {
                requestRepository.findByEventIdAndStatus(eventId, Status.PENDING).stream()
                        .forEach(r -> r.setStatus(Status.REJECTED));
            }
        }
        log.info("Заявка пользователя с id={} на участие в событии с id={} подтверждена пользователем с id={}",
                dto.getRequester(), eventId, userId);
        return dto;
    }

    /**
     * Отклонение заявки на участие в событии
     *
     * @param userId  Long
     * @param eventId Long
     * @param reqId   Long
     * @return ParticipationRequestDto
     */
    @Override
    @Transactional
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        userValidation(userId);
        eventValidation(eventId);
        initiatorValidationWithEventId(userId, eventId);
        eventAndInitiatorRequestValidation(reqId, eventId, userId);
        Request request = requestRepository.findById(reqId).get();
        request.setStatus(Status.REJECTED);
        ParticipationRequestDto dto = RequestMapper.toRequestDto(requestRepository.save(request));
        log.info("Заявка пользователя с id={} на участие в событии с id={} отклонена пользователем с id={}",
                dto.getRequester(), eventId, userId);
        return dto;
    }

    /**
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
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
     * Проверка, является ли пользователь создателем события
     *
     * @param userId  Long
     * @param eventId Long
     */
    private void initiatorValidationWithEventId(Long userId, Long eventId) {
        if (userId != eventRepository.findById(eventId).get().getInitiator().getId()) {
            throw new ValidationException("Только пользователь, создавший событие, может совершить с ним данное действие");
        }
    }

    /**
     * Проверка, является ли пользователь создателем события
     *
     * @param userId      Long
     * @param initiatorId Long
     */
    private void initiatorValidation(long userId, long initiatorId) {
        if (userId != initiatorId) {
            throw new ValidationException("Только пользователь, создавший событие, может совершить с ним данное действие");
        }
    }

    /**
     * Проверка наличия запроса на участие в событии в базе по id
     *
     * @param id Long
     */
    private void requestValidation(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new NotFoundException(String.format("Заявка на участие в событие с id = '%s' не найдена", id));
        }
    }

    /**
     * Проверка наличия запроса на участие в событии в базе по id, а также является ли пользователь создателем события
     *
     * @param reqId   Long
     * @param eventId Long
     * @param userId  Long
     */
    private void eventAndInitiatorRequestValidation(Long reqId, Long eventId, Long userId) {
        Request request = requestRepository.findById(reqId).get();
        if (eventId != request.getEvent().getId()) {
            throw new NotFoundException(String.format("На событие с id = '%s' нет заявки с id = '%s'", eventId, reqId));
        }
        if (userId != request.getEvent().getInitiator().getId()) {
            throw new ValidationException("Только пользователь, создавший событие, может одобрить заявку на участие");
        }
    }
}
