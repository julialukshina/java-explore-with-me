package ru.yandex.practicum.statistics.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.dto.ViewStats;
import ru.yandex.practicum.statistics.exceptions.MyValidationException;
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
public class StatisticsServiceImpl implements StatisticsService{
    private final HitRepository repository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticsServiceImpl(HitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addHit(EndpointHit endpointHit) {
        repository.save(EndpointHitMapper.toHit(endpointHit));
        System.out.println(repository.findById(1L));
    }

    @Override
    public List<ViewStats> getStatistics(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startStat;
        LocalDateTime endStat;
        String app = "service";
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStats = new ViewStats(null, null, 0);
        if(uris.isEmpty()){
            throw new MyValidationException("Uris для подсчета статистики не переданы");
        }
        try {
            startStat= LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8.toString()), formatter);
            endStat= LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8.toString()), formatter);
        }catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
//        try{
//            startStat=LocalDateTime.parse(start, formatter);
//            endStat=LocalDateTime.parse(end,formatter);
//        }catch(MyValidationException e){
//            throw new MyValidationException("Спарсить время не получилось");
//        }
        if(unique){
            for (String uri:
                 uris) {
                viewStats.setUri(uri);
                viewStats.setApp(app);
                viewStats.setHits(repository.getUniqueStatistics(startStat, endStat, uri));
                views.add(viewStats);
            }
        }else{
            for (String uri:
                    uris) {
                viewStats.setUri(uri);
                viewStats.setApp(app);
                viewStats.setHits(repository.getStatistics(startStat, endStat, uri));
                views.add(viewStats);
            }
        }
        return views;
    }
}