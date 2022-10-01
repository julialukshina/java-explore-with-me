package ru.yandex.practicum.service.dto.compilations;

import lombok.*;
import ru.yandex.practicum.service.dto.events.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CompilationDto {
    @NotNull
    @Positive
    private long id;
    private List<EventShortDto> events;
    @NotNull
    @NotBlank
    private boolean pinned;
    @NotNull
    @NotBlank
    private String title;
}
