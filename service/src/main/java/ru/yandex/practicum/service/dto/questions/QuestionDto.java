package ru.yandex.practicum.service.dto.questions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private long id;
    private String text;
    private String answer;
    private String authorName;
    private long eventId;
    private String created;
}
