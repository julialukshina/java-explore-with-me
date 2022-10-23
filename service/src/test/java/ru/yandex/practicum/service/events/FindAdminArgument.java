package ru.yandex.practicum.service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.service.enums.State;

import java.util.List;

/**
 * dto для передачи параметров в тест метода поиска контроллера событий для администраторов
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class FindAdminArgument {
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}