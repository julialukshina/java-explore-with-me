package ru.yandex.practicum.statistics.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.dto.ViewStats;
import ru.yandex.practicum.statistics.exceptions.ValidationException;
import ru.yandex.practicum.statistics.mappers.EndpointHitMapper;
import ru.yandex.practicum.statistics.repositories.HitRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final HitRepository repository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticsServiceImpl(HitRepository repository) {
        this.repository = repository;
    }

    /**
     * Добавление записи статистики
     *
     * @param endpointHit EndpointHit
     */
    @Override
    public void addHit(EndpointHit endpointHit) {
        repository.save(EndpointHitMapper.toHit(endpointHit));
    }

    /**
     * Выдача статистики
     *
     * @param start  String
     * @param end    String
     * @param uris   List<String>
     * @param unique boolean
     * @return List<ViewStats>
     */
    @Override
    public List<ViewStats> getStatistics(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startStat;
        LocalDateTime endStat;
        String app = "service";
        List<ViewStats> views = new ArrayList<>();
        if (uris.isEmpty()) {
            throw new ValidationException("Uris для подсчета статистики не переданы");
        }
        try {
            startStat = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8.toString()), formatter);
            endStat = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8.toString()), formatter);
        } catch (UnsupportedEncodingException e) {
            throw new ValidationException("Время не может быть раскодировано");
        }
        if (unique) {
            for (String uri :
                    uris) {
                ViewStats viewStats = new ViewStats(null, null, 0);
                viewStats.setUri(uri);
                viewStats.setApp(app);
                viewStats.setHits(repository.getUniqueStatistics(startStat, endStat, uri));
                views.add(viewStats);
            }
        } else {
            for (String uri :
                    uris) {
                ViewStats viewStats = new ViewStats(null, null, 0);
                viewStats.setUri(uri);
                viewStats.setApp(app);
                viewStats.setHits(repository.getStatistics(startStat, endStat, uri));
                views.add(viewStats);
            }
        }
        return views;
    }
}
