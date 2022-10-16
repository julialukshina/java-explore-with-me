package ru.yandex.practicum.service.dto.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.locations.Location;
import ru.yandex.practicum.service.dto.users.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EventFullDto {
    private long id;
    @NotNull
    @NotBlank
    private String annotation;
    @NotNull
    @NotBlank
    private CategoryDto category;
    private long confirmedRequests;
    private String createdOn;
    private String description;
    @NotNull
    @NotBlank
    private String eventDate;
    @NotNull
    @NotBlank
    private UserShortDto initiator;
    @NotNull
    @NotBlank
    private Location location;
    @NotNull
    @NotBlank
    private boolean paid;
    private long participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    @NotNull
    @NotBlank
    private String title;
    private long views;
    private String state;
    private boolean commentModeration;
}
