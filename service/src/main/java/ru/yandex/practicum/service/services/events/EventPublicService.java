package ru.yandex.practicum.service.services.events;

import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.models.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart, String rangeEnd, boolean isAvailable, Sort sort, int from, int size, HttpServletRequest request);

    EventFullDto getEventById(Long id, HttpServletRequest request);

    Event getById(Long id);
}
