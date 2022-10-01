package ru.yandex.practicum.service.dto.events;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AdminUpdateEventRequest {
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    private String title;
}
