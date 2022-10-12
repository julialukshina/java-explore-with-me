package ru.yandex.practicum.service.services.questions;

import ru.yandex.practicum.service.dto.questions.NewQuestionDto;
import ru.yandex.practicum.service.dto.questions.QuestionDto;

import java.util.List;

public interface QuestionPrivateService {
    List<QuestionDto> getQuestions(Long userId, Long eventId, int from);

    QuestionDto addQuestion(Long userId, Long eventId, NewQuestionDto dto);

    QuestionDto updateQuestion(Long userId, Long eventId, Long questId, String answer);

    void deleteQuestion(Long userId, Long eventId, Long questId);
}
