package ru.yandex.practicum.service.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EndpointHit {
    private long id;
    private String uri;
    private String app;
    private String ip;
    private String timestamp;
}
