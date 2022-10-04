package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Event;

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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String startStat = LocalDateTime.now().minusDays(30).format(formatter);
    private final String endStat = LocalDateTime.now().plusDays(30).format(formatter);

    @Autowired
    public Statistics(HitClient hitClient) {
        this.hitClient = hitClient;
    }

    public List<EventShortDto> getListEventShortDtoWithViews(List<Event> events){
        List<String> uris = new ArrayList<>();
        Map<String, EventShortDto> uriEventDtos = new HashMap<>();
        List<EventShortDto> eventDtos = events.stream()
                .map(EventShortMapper::toEventShortDto)
                .collect(Collectors.toList());
        for (EventShortDto dto:
                eventDtos) {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("http://localhost:8080/events/"+dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }
        eventDtos.clear();
        List<ViewStats> views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false);
        for (ViewStats view:
                views) {
            EventShortDto dto = uriEventDtos.get(view.getUri());
            dto.setViews(view.getHits());
            eventDtos.add(dto);
        }
        return eventDtos;
    }

    public EventFullDto getEventFullDtoWithViews(EventFullDto dto){
        String uri = "http://localhost:8080/events/"+dto.getId();
        List <String> uris = new ArrayList<>();
        uris.add(uri);
        List <ViewStats> views = (List<ViewStats>)hitClient.getStats(startStat, endStat, uris, false);
        dto.setViews(views.get(0).getHits());
        return dto;
    }

    public List<EventFullDto> getListEventFullDtoWithViews(List<Event> events){
        List<String> uris = new ArrayList<>();
        Map<String, EventFullDto> uriEventDtos = new HashMap<>();
        List<EventFullDto> eventDtos = events.stream()
                .map(EventFullMapper::toEventFullDto)
                .collect(Collectors.toList());
        for (EventFullDto dto:
                eventDtos) {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("http://localhost:8080/events/"+dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }
        eventDtos.clear();
        List<ViewStats> views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false);
        for (ViewStats view:
                views) {
            EventFullDto dto = uriEventDtos.get(view.getUri());
            dto.setViews(view.getHits());
            eventDtos.add(dto);
        }
        return eventDtos;
    }
}
