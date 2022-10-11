package ru.yandex.practicum.service.services.events;

import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.models.Event;

import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart, String rangeEnd, boolean isAvailable, Sort sort, int from, int size);

    EventFullDto getEventById(Long id);

    Event getById(Long id);
}
