package ru.yandex.practicum.service.dto.questions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private long id;
    @NotNull
    @NotBlank
    private String text;
    private String answer;
    @NotNull
    @NotBlank
    private String authorName;
    private String created;
}
