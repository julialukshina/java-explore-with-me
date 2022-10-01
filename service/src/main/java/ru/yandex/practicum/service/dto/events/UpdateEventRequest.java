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
    // TODO: 27.09.2022 в структуре апи этого поля нет, но в примере в методе update у админа есть
    private Boolean requestModeration;
    private String title;
}
