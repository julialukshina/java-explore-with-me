package ru.yandex.practicum.service.services.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;
import ru.yandex.practicum.service.mappers.RequestMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.repositories.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    @Lazy
    @Autowired
    RequestMapper requestMapper;

    @Autowired
    public RequestPrivateServiceImpl(UserRepository userRepository, EventRepository eventRepository,
                                     CategoryRepository categoryRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
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

    private void requestValidation(Long reqId) {
        if (!requestRepository.existsById(reqId)) {
            throw new MyNotFoundException(String.format("Заявка на участие в событие с id = '%s' не найдена", reqId));
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        userValidation(userId);
        return requestRepository.findByRequester(userId).stream()
                .map(request -> requestMapper.toRequestDto(request))
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto postRequest(Long userId, Long eventId) {
        userValidation(userId);
        eventValidation(eventId);
        if (requestRepository.findByEventAndRequester(eventId, userId) != null) {
            throw new MyValidationException("Заявку на участие в событии нельзя добавить повторно");
        }
        Event event = eventRepository.findById(eventId).get();
        if (userId == event.getInitiator().getId()) {
            throw new MyValidationException("Пользователь, создавший событие, не может подать заявку на участие в нем");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new MyValidationException("Подать заявку можно только на опубликованное событие");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests().size()) {
            throw new MyValidationException(String.format("Лимит заявок на событие с id='%s' исчерпан", eventId));
        }
        Request request = new Request(0, LocalDateTime.now(), event, userRepository.findById(userId).get(), Status.PENDING);
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        requestValidation(requestId);
        Request request = requestRepository.findById(requestId).get();

        if (userId != request.getRequester().getId()) {
            throw new MyValidationException("Только пользователь, создавший заявку, может ее отменить");
        }
        request.setStatus(Status.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }
}
