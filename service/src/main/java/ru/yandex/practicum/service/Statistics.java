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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component

public class Statistics {

    private final HitClient hitClient;
    @Lazy
    private final EventFullMapper eventFullMapper;
    @Lazy
    private final EventShortMapper eventShortMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String startStat = LocalDateTime.now().minusDays(30).format(formatter);
    private final String endStat = LocalDateTime.now().plusDays(30).format(formatter);

    @Autowired
    public Statistics(HitClient hitClient, EventFullMapper eventFullMapper, EventShortMapper eventShortMapper) {
        this.hitClient = hitClient;
        this.eventFullMapper = eventFullMapper;
        this.eventShortMapper = eventShortMapper;
    }

    public List<EventShortDto> getListEventShortDtoWithViews(List<Event> events){
        List<String> uris = new ArrayList<>();
        Map<String, EventShortDto> uriEventDtos = new HashMap<>();
        List<EventShortDto> eventDtos = events.stream()
                .map(eventShortMapper::toEventShortDto)
                .collect(Collectors.toList());
        for (EventShortDto dto:
                eventDtos) {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("/events/"+dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }

        List <ViewStats> views = new ArrayList<>();
        try {
            ResponseEntity<Object> response = hitClient.getStats(startStat, endStat, uris, false);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> stats = (List<Map<String, Object>>) response.getBody();
                if (stats != null && stats.size() > 0) {
                    for (Map<String, Object> s:
                            stats) {
                        ViewStats viewStats = new ViewStats(s.get("uri").toString(),
                                s.get("app").toString(),
                                ((Number)s.get("hits")).longValue());
                        views.add(viewStats);
                    }
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        if(!views.isEmpty()){
            for (ViewStats view:views) {
                EventShortDto dto = uriEventDtos.get(view.getUri());
                dto.setViews(view.getHits());
                uriEventDtos.put(view.getUri(), dto);
            }
            eventDtos.clear();
            eventDtos.addAll(uriEventDtos.values());
        }

//        eventDtos.clear();
//        List<ViewStats> views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false);
//        for (ViewStats view:
//                views) {
//            EventShortDto dto = uriEventDtos.get(view.getUri());
//            dto.setViews(view.getHits());
//            eventDtos.add(dto);
//        }
        return eventDtos;
    }

    public EventFullDto getEventFullDtoWithViews(EventFullDto dto){
        String uri = "/events/"+dto.getId();
        List <String> uris = new ArrayList<>();
        uris.add(uri);
        try {
            ResponseEntity<Object> response = hitClient.getStats(startStat, endStat, uris, false);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> stats = (List<Map<String, Object>>) response.getBody();
                if (stats != null && stats.size() > 0) {
                    dto.setViews(((Number)stats.get(0).get("hits")).longValue());
//                    for (Map<String, Object> s:
//                            stats) {
//                        ViewStats viewStats = new ViewStats(s.get("uri").toString(),
//                                s.get("app").toString(),
//                                ((Number)s.get("hits")).longValue());
//                        views.add(viewStats);
//                    }
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
//        List <ViewStats> views = (List<ViewStats>)hitClient.getStats(startStat, endStat, uris, false);
//        if(!views.isEmpty()){
//            dto.setViews(views.get(0).getHits());
//        }
        return dto;
    }

    public List<EventFullDto> getListEventFullDtoWithViews(List<Event> events){
        List<String> uris = new ArrayList<>();
        Map<String, EventFullDto> uriEventDtos = new HashMap<>();
        List<EventFullDto> eventDtos = events.stream()
                .map(eventFullMapper::toEventFullDto)
                .collect(Collectors.toList());
        for (EventFullDto dto:
                eventDtos) {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("/events/"+dto.getId());
            uris.add(String.valueOf(stringBuilder));
            uriEventDtos.put(String.valueOf(stringBuilder), dto);
        }
//        LinkedHashMap <String, Object> views = (LinkedHashMap<String, Object>) hitClient.getStats(startStat, endStat, uris, false).getBody();
     //   List<ViewStats> views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false);
//        List<ViewStats> views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false).getBody();
//if(!views.isEmpty()){
//    for (ViewStats view:views) {
//        EventFullDto dto = uriEventDtos.get(view.getUri());
//        dto.setViews(view.getHits());
//        uriEventDtos.put(view.getUri(), dto);
//    }
//    eventDtos.clear();
//    eventDtos.addAll(uriEventDtos.values());
//}
//        ((LinkedHashMap) hitClient.getStats(startStat, endStat, uris, false).getBody()).get("hits");
//        List<Object> objects = Arrays.asList(hitClient.getStats(startStat, endStat, uris, false).getBody());
//                List<String> strings = new ArrayList<>();
//        for (Object o:
//             objects) {
//            strings.add(o.toString());
//        }
//        List<ViewStats> views = new ArrayList<>();
//        for (String s:
//             strings) {
//            views.add(serializeViewStats(s));
//        }
        List <ViewStats> views = new ArrayList<>();
        try {
            ResponseEntity<Object> response = hitClient.getStats(startStat, endStat, uris, false);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> stats = (List<Map<String, Object>>) response.getBody();
                if (stats != null && stats.size() > 0) {
                    for (Map<String, Object> s:
                         stats) {
                        ViewStats viewStats = new ViewStats(s.get("uri").toString(),
                                s.get("app").toString(),
                                ((Number)s.get("hits")).longValue());
                        views.add(viewStats);
                    }
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
if(!views.isEmpty()){
    for (ViewStats view:views) {
        EventFullDto dto = uriEventDtos.get(view.getUri());
        dto.setViews(view.getHits());
        uriEventDtos.put(view.getUri(), dto);
    }
    eventDtos.clear();
    eventDtos.addAll(uriEventDtos.values());
}
        return eventDtos;
    }

//    private ViewStats serializeViewStats(String s){
//        String[] m = s.split(",");
//        String uri=m[0].substring(4);
//        String app=m[1].substring(4);
//        Long hits = Long.parseLong(m[2].substring(6));
//        System.out.println('\n');
//        System.out.println('\n');
//        System.out.println(uri + "     " + app+ "     " + hits);
//        System.out.println('\n');
//        System.out.println('\n');
//        return new ViewStats(uri, app, hits);
//    }
}
