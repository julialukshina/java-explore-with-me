package ru.yandex.practicum.service.dto.statistics;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ViewStats {
    String uri;
    String app;
    long hits;
}
