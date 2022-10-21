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
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String title;
    private long views;
    private String state;
    private boolean commentModeration;
}
