package ru.yandex.practicum.service.mappers.events;

import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.mappers.CategoryMapper;
import ru.yandex.practicum.service.mappers.users.UserShortDtoMapper;
import ru.yandex.practicum.service.models.Event;

public class EventShortMapper {
    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests().size(),
                event.getEventDate().toString(),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                0);
    }
}
