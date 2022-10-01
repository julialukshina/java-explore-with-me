package ru.yandex.practicum.service.dto.users;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserShortDto {
    @NotNull
    @Positive
    private long id;
    @NotNull
    @NotBlank
    private String name;
}
