package ru.yandex.practicum.service.mappers.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.CategoryMapper;
import ru.yandex.practicum.service.mappers.users.UserShortDtoMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Lazy
@Component
public class EventFullMapper {
    private static StateEnumConverter converter = new StateEnumConverter();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RequestRepository requestRepository;
    @Autowired
    public EventFullMapper(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public EventFullDto toEventFullDto(Event event) {
        long eventConfirmedRequest = 0;
        if(requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED) != null){
            eventConfirmedRequest=requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        }
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                eventConfirmedRequest,
                event.getCreatedOn().format(formatter),
                event.getDescription(),
                event.getEventDate().format(formatter),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() != null ? event.getPublishedOn().format(formatter) : null,
                event.isRequestModeration(),
                event.getTitle(),
                0,
                event.getState().toString());
    }

    // TODO: 25.09.2022  заполнить реквестами

    public Event toEvent(EventFullDto dto) {
        return new Event(dto.getId(),
                dto.getAnnotation(),
                CategoryMapper.toCategory(dto.getCategory()),
                LocalDateTime.parse(dto.getCreatedOn(), formatter),
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate(), formatter),
                UserShortDtoMapper.toUser(dto.getInitiator()),
                dto.isPaid(),
                dto.getParticipantLimit(),
                LocalDateTime.parse(dto.getPublishedOn(), formatter),
                dto.isRequestModeration(),
                dto.getTitle(),
                converter.convert(dto.getState()));
    }
}
