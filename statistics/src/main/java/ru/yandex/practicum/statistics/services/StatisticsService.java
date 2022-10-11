package ru.yandex.practicum.statistics.services;

import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.dto.ViewStats;

import java.util.List;

public interface StatisticsService {
    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStatistics(String start, String end, List<String> uris, boolean unique);
}
