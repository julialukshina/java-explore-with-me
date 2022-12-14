package ru.yandex.practicum.service.controllers.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.events.AdminUpdateEventRequest;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.services.events.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventAdminService service;

    @Autowired
    public EventAdminController(EventAdminService service) {
        this.service = service;
    }


    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Integer> users,
                                        @RequestParam(name = "states", required = false) List<String> states,
                                        @RequestParam(name = "categories", required = false) List<Integer> categories,
                                        @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }


    @PutMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid AdminUpdateEventRequest updateEventRequest) {
        return service.updateEvent(eventId, updateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return service.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return service.rejectEvent(eventId);
    }
}
