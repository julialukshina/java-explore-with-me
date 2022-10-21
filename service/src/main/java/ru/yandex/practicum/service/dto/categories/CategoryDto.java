package ru.yandex.practicum.service.dto.categories;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CategoryDto {
    @Min(value=0)
    private long id;
    @NotNull
    @NotBlank
    private String name;
}
