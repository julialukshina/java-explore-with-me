package ru.yandex.practicum.service.mappers.questions;

import ru.yandex.practicum.service.dto.questions.QuestionDto;
import ru.yandex.practicum.service.models.Question;

import java.time.format.DateTimeFormatter;

public class QuestionMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static QuestionDto toQuestionDto(Question question) {
        return new QuestionDto(question.getId(),
                question.getText(),
                question.getAnswer(),
                question.getAuthor().getName(),
                question.getCreated().format(formatter));
    }
}
