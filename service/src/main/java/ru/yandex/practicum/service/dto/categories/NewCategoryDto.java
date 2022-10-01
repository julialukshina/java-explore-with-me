package ru.yandex.practicum.service.dto.categories;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NewCategoryDto {
    @NotNull
    @NotBlank
    private String name;
}
