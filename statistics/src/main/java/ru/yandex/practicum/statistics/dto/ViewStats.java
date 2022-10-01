package ru.yandex.practicum.statistics.dto;

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
