package ru.yandex.practicum.service.dto.categories;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CategoryDto {
    @NotNull
    @Positive
    private long id;
    @NotNull
    @NotBlank
    private String name;
}
