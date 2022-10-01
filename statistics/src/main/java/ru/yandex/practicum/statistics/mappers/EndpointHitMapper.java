package ru.yandex.practicum.statistics.mappers;

import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.model.Hit;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(Hit hit){
        return new EndpointHit(hit.getId(),
                hit.getUri(),
                hit.getApp(),
                hit.getIp(),
                hit.getTimestamp());
    }

    public static Hit toHit(EndpointHit endpointHit){
        return new Hit(endpointHit.getId(),
                endpointHit.getUri(),
                endpointHit.getApp(),
                endpointHit.getIp(),
                endpointHit.getTimestamp());
    }
}
