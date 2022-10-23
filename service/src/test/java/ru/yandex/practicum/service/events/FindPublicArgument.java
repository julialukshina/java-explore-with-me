package ru.yandex.practicum.service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.service.enums.Sort;

import java.util.List;

/**
 * dto для передачи параметров в тест метода поиска публичного контроллера событий
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class FindPublicArgument {
    private String text;
    private Boolean paid;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;
}