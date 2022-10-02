package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.MyPageable;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.events.NewEventDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;
import ru.yandex.practicum.service.mappers.RequestMapper;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private final Statistics statistics;
    @Lazy
    @Autowired
    RequestMapper requestMapper;

    @Autowired
    public EventPrivateServiceImpl(UserRepository userRepository, EventRepository eventRepository, CategoryRepository categoryRepository, RequestRepository requestRepository, Statistics statistics) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statistics = statistics;
    }

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, int from, int size) {
        userValidation(userId);
        Pageable pageable = MyPageable.of(from, size);

        return statistics.getListEventShortDtoWithViews((List<Event>) eventRepository.findByInitiator(userId, pageable));

//        return eventRepository.findByInitiator(userId, pageable).stream()
//                .map(EventShortMapper::toEventShortDto)
//                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        userValidation(userId);
        eventValidation(updateEventRequest.getEventId());
        if (LocalDateTime.parse(updateEventRequest.getEventDate()).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new MyValidationException("Изменение события доступно не менее, чем за два часа до его наступления");
        }

        Event event = eventRepository.findById(updateEventRequest.getEventId()).get();
        initiatorValidation(userId, event.getInitiator().getId());
        if (event.getState().equals(State.PUBLISHED)) {
            throw new MyValidationException("Только отмененные или ожидающие публикации события могут быть обновлены");
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
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate()));
        } else {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new MyValidationException("Событие не может состояться ранее, чем через два часа после его обновления");
            }
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        // TODO: 27.09.2022 возможно, не требуется (в примере нет)
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }


        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (event.getState().equals(State.CANCELED)) {
            event.setState(State.PENDING);
        }

        // TODO: 02.10.2022 нужны ли здесь просмотры?

        return EventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        return statistics.getEventFullDtoWithViews(EventFullMapper.toEventFullDto(event));
//        return EventFullMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto postEvent(Long userId, NewEventDto dto) {
        userValidation(userId);
        LocalDateTime now = LocalDateTime.now();
        if (LocalDateTime.parse(dto.getEventDate()).isBefore(now.plusHours(2))) {
            throw new MyValidationException("Событие может быть опубликовано не менее, чем за два часа до его наступления");
        }
        Event event = new Event(0,
                dto.getAnnotation(),
                categoryRepository.findById(dto.getCategory()).get(),
                new ArrayList<>(),
                now,
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate()),
                userRepository.findById(userId).get(),
                dto.isPaid(),
                dto.getParticipantLimit(),
                null,
                dto.isRequestModeration(),
                dto.getTitle(),
                State.PENDING);
        return EventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        if (!event.getState().equals(State.PENDING)) {
            throw new MyValidationException("Только ожидающее модерации событие может быть отменено");
        }
        event.setState(State.CANCELED);
        return EventFullMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        initiatorValidation(userId, event.getInitiator().getId());
        return requestRepository.findByEvent(eventId).stream()
                .map(request -> requestMapper.toRequestDto(request))
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        userValidation(userId);
        eventValidation(eventId);
        initiatorValidation(userId, eventId);
        requestValidation(reqId);
        eventAndInitiatorRequestValidation(reqId, eventId, userId);
        Request request = requestRepository.findById(reqId).get();
        request.setStatus(Status.CONFIRMED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        userValidation(userId);
        eventValidation(eventId);
        initiatorValidation(userId, eventId);
        eventAndInitiatorRequestValidation(reqId, eventId, userId);
        Request request = requestRepository.findById(reqId).get();
        request.setStatus(Status.REJECTED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }

    private void initiatorValidation(Long userId, Long eventId) {
        if (userId != eventRepository.findById(eventId).get().getInitiator().getId()) {
            throw new MyValidationException("Только пользователь, создавший событие, может совершить с ним данное действие");
        }
    }

    private void requestValidation(Long reqId) {
        if (!requestRepository.existsById(reqId)) {
            throw new MyNotFoundException(String.format("Заявка на участие в событие с id = '%s' не найдена", reqId));
        }
    }

    private void eventAndInitiatorRequestValidation(Long reqId, Long eventId, Long userId) {
        Request request = requestRepository.findById(reqId).get();
        if (eventId != request.getEvent().getId()) {
            throw new MyNotFoundException(String.format("На событие с id = '%s' нет заявки с id = '%s'", eventId, reqId));
        }
        if (userId != request.getEvent().getInitiator().getId()) {
            throw new MyValidationException("Только пользователь, создавший событие, может одобрить заявку на участие");
        }
    }
}
