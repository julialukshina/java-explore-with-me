package ru.yandex.practicum.service.dto.compilations;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NewCompilationDto {
    @NotNull
    @NotBlank
    private List<Long> events;
    @NotNull
    @NotBlank
    private boolean pinned;
    @NotNull
    @NotBlank
    private String title;
}
