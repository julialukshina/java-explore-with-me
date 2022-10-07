package ru.yandex.practicum.service.dto.events;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateEventRequest {
    @NotNull
    @Positive
    private long eventId;
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String title;
}
