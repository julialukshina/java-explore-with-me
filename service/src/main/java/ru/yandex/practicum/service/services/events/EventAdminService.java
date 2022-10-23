package ru.yandex.practicum.service.services.events;

import ru.yandex.practicum.service.dto.events.AdminUpdateEventRequest;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;

import java.util.List;


public interface EventAdminService {

    List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest updateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);
}
