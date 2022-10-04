package ru.yandex.practicum.statistics.dto;

import lombok.*;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EndpointHit {
    private long id;
    String uri;
    String app;
    String ip;
    String timestamp;
}
