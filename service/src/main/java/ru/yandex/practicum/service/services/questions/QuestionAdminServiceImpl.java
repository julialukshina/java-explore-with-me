package ru.yandex.practicum.service.services.questions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.repositories.QuestionRepository;

@Service
@Slf4j
public class QuestionAdminServiceImpl implements QuestionAdminService {
    private final QuestionRepository repository;

    @Autowired
    public QuestionAdminServiceImpl(QuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteQuestion(Long questId) {
        questionValidation(questId);
        repository.deleteById(questId);
        log.info("Вопрос с id={} удален администратором", questId);
    }

    /**
     * Проверка наличия вопроса в базе по id
     *
     * @param id Long
     */
    private void questionValidation(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("Вопрос с id = '%s' не найден", id));
        }
    }
}
