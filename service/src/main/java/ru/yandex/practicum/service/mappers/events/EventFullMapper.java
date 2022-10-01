package ru.yandex.practicum.service.mappers.events;

import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.mappers.CategoryMapper;
import ru.yandex.practicum.service.mappers.users.UserShortDtoMapper;
import ru.yandex.practicum.service.models.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventFullMapper {
    private static StateEnumConverter converter = new StateEnumConverter();

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests().size(),
                event.getCreatedOn().toString(),
                event.getDescription(),
                event.getEventDate().toString(),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().toString(),
                event.isRequestModeration(),
                event.getTitle(),
                0,
                event.getState().toString());
    }

    // TODO: 25.09.2022  заполнить реквестами
    public static Event toEvent(EventFullDto dto) {
        return new Event(dto.getId(),
                dto.getAnnotation(),
                CategoryMapper.toCategory(dto.getCategory()),
                new ArrayList<>(),
                LocalDateTime.parse(dto.getCreatedOn()),
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate()),
                UserShortDtoMapper.toUser(dto.getInitiator()),
                dto.isPaid(),
                dto.getParticipantLimit(),
                LocalDateTime.parse(dto.getPublishedOn()),
                dto.isRequestModeration(),
                dto.getTitle(),
                converter.convert(dto.getState()));
    }
}
