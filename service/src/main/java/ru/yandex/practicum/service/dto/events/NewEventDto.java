package ru.yandex.practicum.service.dto.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NewEventDto {
    @NotNull
    @NotBlank
    private String annotation;
    @NotNull
    @NotBlank
    private long category;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotBlank
    private String eventDate;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    @NotNull
    @NotBlank
    private String title;
}
