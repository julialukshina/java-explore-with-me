package ru.yandex.practicum.service.controllers.questions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.questions.NewQuestionDto;
import ru.yandex.practicum.service.dto.questions.QuestionDto;
import ru.yandex.practicum.service.services.questions.QuestionPrivateService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/questions")
@Slf4j
@Validated
public class QuestionPrivateController {
    private final QuestionPrivateService service;

    @Autowired
    public QuestionPrivateController(QuestionPrivateService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuestionDto> getQuestions(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        return service.getQuestions(userId, eventId, from);
    }

    @PostMapping
    public QuestionDto addQuestion(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody NewQuestionDto dto) {
        return service.addQuestion(userId, eventId, dto);
    }

    @PatchMapping("/{questId}")
    public QuestionDto updateQuestion(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Long questId,
                                     @RequestBody @NotBlank String answer) {
        return service.updateQuestion(userId, eventId, questId, answer);
    }

    @DeleteMapping("/{questId}")
    public void deleteQuestion(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @PathVariable Long questId) {
        service.deleteQuestion(userId, eventId, questId);
    }
}
