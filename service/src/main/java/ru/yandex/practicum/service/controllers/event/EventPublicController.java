package ru.yandex.practicum.service.controllers.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.enums.SortEnumConverter;
import ru.yandex.practicum.service.exeptions.SortInvalidException;
import ru.yandex.practicum.service.services.events.EventPublicService;
import ru.yandex.practicum.statistics.dto.EndpointHit;

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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public EventPublicController(EventPublicService service, SortEnumConverter converter, HitClient client) {
        this.service = service;
        this.converter = converter;
        this.client = client;
    }

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                         @RequestParam(name = "categories", required = false) List <Integer> categories,
                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                         @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                         @RequestParam(name = "isAvailable", defaultValue = "false") boolean isAvailable,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        Sort sort1=null;
        if(sort!=null){
           sort1= converter.convert(sort);
           if(sort1==Sort.UNSUPPORTED_SORT){
               throw new SortInvalidException();
           }
       }

        client.createHit(new EndpointHit(0,
                request.getRequestURI(),
                app,
                request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(formatter))));

        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, isAvailable, sort1, from, size);
    }

    @GetMapping(value = "/{id}") //возвращает вещь по Id
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        client.createHit(new EndpointHit(0,
                request.getRequestURI(),
                app,
                request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(formatter))));

        return service.getEventById(id);
    }
}
