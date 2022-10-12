package ru.yandex.practicum.service.services.questions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.questions.NewQuestionDto;
import ru.yandex.practicum.service.dto.questions.QuestionDto;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.questions.QuestionMapper;
import ru.yandex.practicum.service.models.Question;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.QuestionRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionPrivateServiceImpl implements QuestionPrivateService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final static int SIZE = 20;

    @Autowired
    public QuestionPrivateServiceImpl(EventRepository eventRepository, UserRepository userRepository, QuestionRepository questionRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * Выдача списка вопросов по событию
     *
     * @param userId  Long
     * @param eventId Long
     * @param from    int
     * @return List<QuestionDto>
     */

    @Override
    @Transactional
    public List<QuestionDto> getQuestions(Long userId, Long eventId, int from) {
        userValidation(userId);
        eventValidation(eventId);
        Pageable pageable = PageRequest.of(from, SIZE);
        List<QuestionDto> dtos = questionRepository.findByEventId(eventId, pageable).stream()
                .map(QuestionMapper::toQuestionDto)
                .collect(Collectors.toList());
        log.info("Пользователю с id={} предоставлены вопросы к событию с id={}", userId, eventId);
        return dtos;
    }

    /**
     * Добавление вопроса
     *
     * @param userId  Long
     * @param eventId Long
     * @param dto     NewQuestionDto
     * @return QuestionDto
     */
    @Override
    @Transactional
    public QuestionDto addQuestion(Long userId, Long eventId, NewQuestionDto dto) {
        userValidation(userId);
        eventByEventDateValidation(eventId);
        Question question = questionRepository.save(new Question(0,
                dto.getText(),
                null,
                eventRepository.findById(eventId).get(),
                userRepository.findById(userId).get(),
                LocalDateTime.now()));
        log.info("Вопрос с id={} создан", question.getId());
        return QuestionMapper.toQuestionDto(question);
    }

    /**
     * Добавление автором события ответа на вопрос
     *
     * @param userId  Long
     * @param eventId Long
     * @param questId Long
     * @param answer  String
     * @return QuestionDto
     */
    @Override
    @Transactional
    public QuestionDto updateQuestion(Long userId, Long eventId, Long questId, String answer) {
        userValidation(userId);
        eventByEventDateValidation(eventId);
        questionValidation(eventId, questId);
        creatorValidation(userId, eventId);
        Question question = questionRepository.findById(questId).get();
        question.setAnswer(answer);
        QuestionDto dto = QuestionMapper.toQuestionDto(questionRepository.save(question));
        log.info("К вопросу с id={} добавлен ответ", questId);
        return dto;
    }

    /**
     * Удаление вопроса его автором
     *
     * @param userId  Long
     * @param eventId Long
     * @param questId Long
     */
    @Override
    @Transactional
    public void deleteQuestion(Long userId, Long eventId, Long questId) {
        userValidation(userId);
        eventByEventDateValidation(eventId);
        questionValidation(eventId, questId);
        authorValidation(userId, questId);
        questionRepository.deleteById(questId);
        log.info("Вопрос с id={} удален автором вопроса", questId);
    }


    /**
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    /**
     * Проверка наличия события в базе по id
     *
     * @param id Long
     */
    private void eventValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
    }

    /**
     * Проверка наличия события в базе по id и что данное событие еще не состоялось
     *
     * @param id Long
     */
    private void eventByEventDateValidation(Long id) {
        LocalDateTime now = LocalDateTime.now();
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
        if (eventRepository.findById(id).get().getEventDate().isBefore(now)) {
            throw new ValidationException("Вопрос может быть задан только к событию, которое еще не состоялось");
        }
    }

    /**
     * Проверка наличия вопроса в базе по id и что он относится к данному событию
     *
     * @param eventId Long
     * @param questId Long
     */
    private void questionValidation(Long eventId, Long questId) {
        if (!questionRepository.existsById(questId)) {
            throw new NotFoundException(String.format("Вопрос с id = '%s' не найден", questId));
        }
        if (questionRepository.findById(questId).get().getEvent().getId() != eventId) {
            throw new ValidationException(String.format("Вопрос с id = '%s' не относится к событию с id = '%s'", questId, eventId));
        }
    }


    /**
     * Проверка, является ли пользователь автором события
     *
     * @param userId  Long
     * @param eventId Long
     */
    private void creatorValidation(Long userId, Long eventId) {
        if (eventRepository.findById(eventId).get().getInitiator().getId() != userId) {
            throw new ValidationException(String.format("Пользователь с id = '%s' не является автором события с id = '%s'", userId, eventId));

        }
    }

    /**
     * Проверка, является ли пользователь автором вопроса
     *
     * @param userId  Long
     * @param questId Long
     */
    private void authorValidation(Long userId, Long questId) {
        if (questionRepository.findById(questId).get().getAuthor().getId() != userId) {
            throw new ValidationException(String.format("Пользователь с id = '%s' не является автором вопроса с id = '%s'", userId, questId));

        }
    }
}
