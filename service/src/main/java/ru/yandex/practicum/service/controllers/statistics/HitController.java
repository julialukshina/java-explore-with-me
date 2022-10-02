package ru.yandex.practicum.service.controllers.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.clients.HitClient;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class HitController {
    private final HitClient hitClient;
}
