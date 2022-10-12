package ru.yandex.practicum.service.services.comments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.dto.comments.NewCommentDto;
import ru.yandex.practicum.service.enums.CommentStatus;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.comments.CommentMapper;
import ru.yandex.practicum.service.models.Comment;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CommentRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentPrivateServiceImpl implements CommentPrivateService{

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final static int SIZE = 20;

    @Autowired
    public CommentPrivateServiceImpl(EventRepository eventRepository, CommentRepository commentRepository, UserRepository userRepository, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    /**
     * Выдача списка комментариев по событию
     *
     * @param userId  Long
     * @param eventId Long
     * @param from    int
     * @return List<CommentDto>
     */
    @Override
    @Transactional
    public List<CommentDto> getComments(Long userId, Long eventId, int from) {
       Pageable pageable = PageRequest.of(from, SIZE);
        userValidation(userId);
        eventValidation(eventId);
        List<CommentDto> dtos = commentRepository.findByEventId(eventId, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        log.info("Пользователю с id={} предоставлены комментарии по событию с id={}", userId, eventId);
        return dtos;
    }

    /**
     * Создание комментария
     *
     * @param userId Long
     * @param eventId Long
     * @param dto NewCommentDto
     * @return List<CommentDto>
     */
    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        userValidation(userId);
        eventValidation(eventId);
        Event event = eventRepository.findById(eventId).get();
        if(event.getParticipantLimit() !=0){
            if(!requestRepository.existsByEventIdAndRequesterId(eventId, userId)||
                    !requestRepository.findByEventIdAndRequesterId(eventId,userId).getStatus().equals(Status.CONFIRMED)){
                throw new ValidationException("Только пользователь с подтвержденной заявкой может оставить комментарий к событию");
            }
        }
 Comment comment = commentRepository.save(new Comment(0,
        dto.getText(),
        event,
        userRepository.findById(userId).get(),
        LocalDateTime.now(),
        CommentStatus.NO_CHANGES));
 log.info("Комментарий с id={} создано", comment.getId());
        return CommentMapper.toCommentDto(comment);
    }

    /**
     * Обновление комментария
     *
     * @param userId Long
     * @param eventId Long
     * @param commId Long
     * @param text String
     * @return CommentDto
     */
    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commId, String text) {
        userValidation(userId);
        eventValidation(eventId);
        commentValidation(commId);
        eventCommentValidation(eventId, commId);
        authorValidation(userId, commId);
        Comment comment = commentRepository.findById(commId).get();
        comment.setText(text);
        comment.setCommentStatus(CommentStatus.EDITED);
        CommentDto dto = CommentMapper.toCommentDto(commentRepository.save(comment));
        log.info("Комментарий с id={} обновлен", commId);
        return dto;
    }

    /**
     * Удаление комментария его автором
     *
     * @param userId Long
     * @param eventId Long
     * @param commId Long
     */
    @Override
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commId) {
        userValidation(userId);
        eventValidation(eventId);
        commentValidation(commId);
        eventCommentValidation(eventId, commId);
        authorValidation(userId, commId);
        commentRepository.deleteById(commId);
        log.info("Комментарий с id={} удален автором комментария", commId);
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
        LocalDateTime now = LocalDateTime.now();
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Событие с id = '%s' не найдено", id));
        }
        if(eventRepository.findById(id).get().getEventDate().isAfter(now)){
            throw new ValidationException("Комментарий может быть добавлен только к событию, которое уже состоялось");
        }
    }

    /**
     * Проверка наличия комментария в базе по id
     * @param id Long
     */
    private void commentValidation(Long id){
        if(!commentRepository.existsById(id)){
            throw new NotFoundException(String.format("Комментарий с id = '%s' не найден", id));
        }
    }

    /**
     * Проверка, создан ли комментарий к данному событию
     *
     * @param eventId Long
     * @param commId Long
     */
    private void eventCommentValidation (Long eventId, Long commId){
        if(requestRepository.findById(commId).get().getEvent().getId()!=eventId){
            throw new ValidationException(String.format("Комментарий с id = '%s' не относится к событию с id = '%s'", commId, eventId) );
        }
    }

    /**
     * Проверка, является ли пользователь автором комментария
     *
     * @param userId Long
     * @param commId Long
     */
    private void authorValidation(Long userId, Long commId){
        if(commentRepository.findById(commId).get().getAuthor().getId()!=userId){
            throw new ValidationException(String.format("Пользователь с id = '%s' не является автором комментария с id = '%s'", userId, commId));

        }
    }

}
