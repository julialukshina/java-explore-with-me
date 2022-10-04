package ru.yandex.practicum.statistics.mappers;

import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {
   static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static EndpointHit toEndpointHit(Hit hit){
        return new EndpointHit(hit.getId(),
                hit.getUri(),
                hit.getApp(),
                hit.getIp(),
                hit.getTimestamp().toString());
    }

    public static Hit toHit(EndpointHit endpointHit){
        return new Hit(endpointHit.getId(),
                endpointHit.getUri(),
                endpointHit.getApp(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp(), formatter));
    }
}
