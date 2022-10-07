package ru.yandex.practicum.service.services.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;
import ru.yandex.practicum.service.mappers.requests.RequestMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public RequestPrivateServiceImpl(UserRepository userRepository, EventRepository eventRepository,
                                     RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    /**
     * Выдача списка запросов на участие пользователя
     *
     * @param userId Long
     * @return List<ParticipationRequestDto>
     */
    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        userValidation(userId);
        List<ParticipationRequestDto> dto = requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("Пользователю с id={} предоставлен список его заявок на участие в событиях", userId);
        return dto;
    }

    /**
     * Создание заявки на участие в событии
     *
     * @param userId  Long
     * @param eventId Long
     * @return ParticipationRequestDto
     */
    @Override
    public ParticipationRequestDto postRequest(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        long eventConfirmedRequest = 0;
        if (requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED) != null) {
            eventConfirmedRequest = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId) != null) {
            throw new MyValidationException("Заявку на участие в событии нельзя добавить повторно");
        }
        Event event = eventRepository.findById(eventId).get();
        if (userId == event.getInitiator().getId()) {
            throw new MyValidationException("Пользователь, создавший событие, не может подать заявку на участие в нем");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new MyValidationException("Подать заявку можно только на опубликованное событие");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == eventConfirmedRequest) {
            throw new MyValidationException(String.format("Лимит заявок на событие с id='%s' исчерпан", eventId));
        }
        Request request = new Request(0, LocalDateTime.now(), event, userRepository.findById(userId).get(), Status.PENDING);
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        ParticipationRequestDto dto = RequestMapper.toRequestDto(requestRepository.save(request));
        log.info("Заявка с id={} создана", dto.getId());
        return dto;
    }

    /**
     * Отмена заявки на участие в событии
     *
     * @param userId    Long
     * @param requestId Long
     * @return ParticipationRequestDto
     */
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        requestValidation(requestId);
        Request request = requestRepository.findById(requestId).get();

        if (userId != request.getRequester().getId()) {
            throw new MyValidationException("Только пользователь, создавший заявку, может ее отменить");
        }
        request.setStatus(Status.CANCELED);
        ParticipationRequestDto dto = RequestMapper.toRequestDto(requestRepository.save(request));
        log.info("Заявка на участие в событии с id={} отменена пользователем с id={}", requestId, userId);
        return dto;
    }

    /**
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    /**
     * Проверка наличия события в базе по id
     *
     * @param id Long
     */
    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }

    /**
     * Проверка наличия заявки на участие в событии в базе по id
     *
     * @param id Long
     */
    private void requestValidation(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Заявка на участие в событие с id = '%s' не найдена", id));
        }
    }
}
