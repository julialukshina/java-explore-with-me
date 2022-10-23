package ru.yandex.practicum.service.dto.events;

import lombok.*;
import ru.yandex.practicum.service.dto.locations.Location;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @NotBlank
    private String annotation;
    @Min(value=1)
    private long category;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotBlank
    private String eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    @NotNull
    @NotBlank
    private String title;
    private boolean commentModeration;
}
