package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Event;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component

public class Statistics {

    private final HitClient hitClient;
    @Lazy
    private final EventFullMapper eventFullMapper;
    @Lazy
    private final EventShortMapper eventShortMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String startStat = LocalDateTime.now().minusDays(30).format(formatter);
    private final String endStat = LocalDateTime.now().plusDays(30).format(formatter);
    private final Boolean unique = false;

    @Autowired
    public Statistics(HitClient hitClient, EventFullMapper eventFullMapper, EventShortMapper eventShortMapper) {
        this.hitClient = hitClient;
        this.eventFullMapper = eventFullMapper;
        this.eventShortMapper = eventShortMapper;
    }

    /**
     * Выдача списка EventShortDto c заполненной статистикой
     *
     * @param events List<Event>
     * @return List<EventShortDto>
     */
    @Transactional
    public List<EventShortDto> getListEventShortDtoWithViews(List<Event> events) {
        List<String> uris = new ArrayList<>();
        Map<String, EventShortDto> uriEventDtos = new HashMap<>();
        List<EventShortDto> eventDtos = events.stream()
                .map(eventShortMapper::toEventShortDto)
                .collect(Collectors.toList());
        for (EventShortDto dto :
                eventDtos) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/events/").append(dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }
        List<ViewStats> views = getViewStats(startStat, endStat, uris, unique);
        if (!views.isEmpty()) {
            for (ViewStats view : views) {
                EventShortDto dto = uriEventDtos.get(view.getUri());
                dto.setViews(view.getHits());
                uriEventDtos.put(view.getUri(), dto);
            }
            eventDtos.clear();
            eventDtos.addAll(uriEventDtos.values());
        }

        return eventDtos;
    }

    /**
     * Выдача EventFullDto c заполненной статистикой
     *
     * @param dto EventFullDto
     * @return EventFullDto
     */
    @Transactional
    public EventFullDto getEventFullDtoWithViews(EventFullDto dto) {
        String uri = "/events/" + dto.getId();
        List<String> uris = new ArrayList<>();
        uris.add(uri);

//        try {
//            ResponseEntity<Object> response = hitClient.getStats(startStat, endStat, uris, false);
//            if (response.getStatusCode() == HttpStatus.OK) {
//                List<Map<String, Object>> stats = (List<Map<String, Object>>) response.getBody();
//                if (stats != null && stats.size() > 0) {
//                    dto.setViews(((Number) stats.get(0).get("hits")).longValue());
//                }
//            }
//        } catch (Exception ex) {
//            ex.getMessage();
//        }
        List<ViewStats> views = getViewStats(startStat, endStat, uris, unique);
        if (!views.isEmpty()) {
            dto.setViews(views.get(0).getHits());
        }

        return dto;
    }

    /**
     * Выдача списка EventFullDto c заполненной статистикой
     *
     * @param events List<Event>
     * @return List<EventFullDto>
     */
    @Transactional
    public List<EventFullDto> getListEventFullDtoWithViews(List<Event> events) {
        List<String> uris = new ArrayList<>();
        Map<String, EventFullDto> uriEventDtos = new HashMap<>();
        List<EventFullDto> eventDtos = events.stream()
                .map(eventFullMapper::toEventFullDto)
                .collect(Collectors.toList());
        for (EventFullDto dto :
                eventDtos) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/events/").append(dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }
        List<ViewStats> views = getViewStats(startStat, endStat, uris, unique);
        if (!views.isEmpty()) {
            for (ViewStats view : views) {
                EventFullDto dto = uriEventDtos.get(view.getUri());
                dto.setViews(view.getHits());
                uriEventDtos.put(view.getUri(), dto);
            }
            eventDtos.clear();
            eventDtos.addAll(uriEventDtos.values());
        }
        return eventDtos;
    }

    public List<ViewStats> getViewStats(String startStat, String endStat, List<String> uris, boolean unique) {
        List<ViewStats> views = new ArrayList<>();
        try {
            ResponseEntity<Object> response = hitClient.getStats(startStat, endStat, uris, unique);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> stats = (List<Map<String, Object>>) response.getBody();
                if (stats != null && stats.size() > 0) {
                    for (Map<String, Object> s :
                            stats) {
                        ViewStats viewStats = new ViewStats(s.get("uri").toString(),
                                s.get("app").toString(),
                                ((Number) s.get("hits")).longValue());
                        views.add(viewStats);
                    }
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return views;
    }

}
