package ru.yandex.practicum.statistics.controlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.dto.ViewStats;
import ru.yandex.practicum.statistics.services.StatisticsService;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping
@Slf4j
@Validated
public class StatisticsController {

    private final StatisticsService service;
@Autowired
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public void addHit(@RequestBody EndpointHit endpointHit) {
        service.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStatistics(@RequestParam(name="start") @NotNull String start,
                                         @RequestParam(name="end") @NotNull String end,
                                         @RequestParam(name="uris") List<String> uris,
                                         @RequestParam(name = "unique", defaultValue = "false") Boolean unique){
    return service.getStatistics(start, end, uris, unique);
    }
}
