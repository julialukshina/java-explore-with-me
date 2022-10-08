package ru.yandex.practicum.service.controllers.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.statistics.EndpointHit;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.enums.SortEnumConverter;
import ru.yandex.practicum.service.exeptions.SortInvalidException;
import ru.yandex.practicum.service.services.events.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
public class EventPublicController {
    private final SortEnumConverter converter;
    private final EventPublicService service;
    private final HitClient client;
    private final String app = "service";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EventPublicController(EventPublicService service, SortEnumConverter converter, HitClient client) {
        this.service = service;
        this.converter = converter;
        this.client = client;
    }

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                         @RequestParam(name = "categories", required = false) List<Integer> categories,
                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                         @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        Sort sort1 = Sort.NO_SORT;
        if (sort != null && !sort.isBlank() && !sort.isEmpty()) {
            sort1 = converter.convert(sort);
            if (sort1 == Sort.UNSUPPORTED_SORT) {
                throw new SortInvalidException();
            }
        }

        client.createHit(new EndpointHit(0,
                request.getRequestURI(),
                app,
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter)));


        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort1, from, size);
    }

    @GetMapping(value = "/{id}") //возвращает вещь по Id
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        client.createHit(new EndpointHit(0,
                request.getRequestURI(),
                app,
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter)));

        return service.getEventById(id);
    }
}
