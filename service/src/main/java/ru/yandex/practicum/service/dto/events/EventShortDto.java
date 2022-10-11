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
public class EventShortDto {
    private long id;
    @NotNull
    @NotBlank
    private String annotation;
    @NotNull
    @NotBlank
    private CategoryDto category;
    private long confirmedRequests;
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
    @NotNull
    @NotBlank
    private String title;
    private long views;
}
