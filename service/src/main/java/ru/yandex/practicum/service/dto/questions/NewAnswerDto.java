package ru.yandex.practicum.service.dto.questions;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NewAnswerDto {
    @NotNull
    @NotBlank
    private String answer;
}
