package ru.yandex.practicum.service.mappers.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.locations.Location;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.mappers.users.UserShortDtoMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Lazy
@Component
public class EventFullMapper {
    private final StateEnumConverter converter = new StateEnumConverter();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RequestRepository requestRepository;
    @Lazy
    private final UserShortDtoMapper userShortDtoMapper;

    @Autowired
    public EventFullMapper(RequestRepository requestRepository, UserShortDtoMapper userShortDtoMapper) {
        this.requestRepository = requestRepository;
        this.userShortDtoMapper = userShortDtoMapper;
    }

    public EventFullDto toEventFullDto(Event event) {
        long eventConfirmedRequest = 0;
        if (requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED) != null) {
            eventConfirmedRequest = requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        }
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                eventConfirmedRequest,
                event.getCreatedOn().format(formatter),
                event.getDescription(),
                event.getEventDate().format(formatter),
                userShortDtoMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() != null ? event.getPublishedOn().format(formatter) : null,
                event.isRequestModeration(),
                event.getTitle(),
                0,
                event.getState().toString(),
                event.isCommentModeration());
    }

    public Event toEvent(EventFullDto dto) {
        return new Event(dto.getId(),
                dto.getAnnotation(),
                CategoryMapper.toCategory(dto.getCategory()),
                LocalDateTime.parse(dto.getCreatedOn(), formatter),
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate(), formatter),
                userShortDtoMapper.toUser(dto.getInitiator()),
                dto.getLocation().getLat(),
                dto.getLocation().getLon(),
                dto.isPaid(),
                dto.getParticipantLimit(),
                dto.getPublishedOn() != null ? LocalDateTime.parse(dto.getPublishedOn(), formatter) : null,
                dto.isRequestModeration(),
                dto.getTitle(),
                converter.convert(dto.getState()),
                dto.isCommentModeration());
    }
}
