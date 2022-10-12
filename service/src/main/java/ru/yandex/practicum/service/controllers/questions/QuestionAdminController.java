package ru.yandex.practicum.service.controllers.questions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.services.questions.QuestionAdminService;

@RestController
@Slf4j
@Validated
public class QuestionAdminController {
    private final QuestionAdminService service;

    @Autowired
    public QuestionAdminController(QuestionAdminService service) {
        this.service = service;
    }
    @DeleteMapping("/admin/questions/{questId}")
    public void deleteQuestion(@PathVariable Long questId){
        service.deleteQuestion(questId);
    }
}
