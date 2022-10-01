package ru.yandex.practicum.service.controllers.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.events.NewEventDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.services.events.EventPrivateService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
public class EventPrivateController {
    private final EventPrivateService service;

    @Autowired
    public EventPrivateController(EventPrivateService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventShortDto> getEventsOfUser(@PathVariable Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return service.getEventsOfUser(userId, from, size);
    }

    @PatchMapping
    public EventFullDto updateEvent(@PathVariable Long userId, @RequestBody UpdateEventRequest updateEventRequest) {
        return service.updateEvent(userId, updateEventRequest);
    }

    @PostMapping
    public EventFullDto postEvent(@PathVariable Long userId, @RequestBody NewEventDto dto) {
        return service.postEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.cancelEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId) {
        return service.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @PathVariable Long reqId) {
        return service.rejectRequest(userId, eventId, reqId);
    }

}
