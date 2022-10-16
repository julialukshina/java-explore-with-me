package ru.yandex.practicum.service.services.comments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.repositories.CommentRepository;

@Service
@Slf4j
public class CommentAdminServiceImpl implements CommentAdminService {
    private final CommentRepository repository;

    @Autowired
    public CommentAdminServiceImpl(CommentRepository repository) {
        this.repository = repository;
    }

    /**
     * Удаление комментария администратором
     *
     * @param commId Long
     */
    @Override
    @Transactional
    public void deleteComment(Long commId) {
        commentValidation(commId);
        repository.deleteById(commId);
        log.info("Комментарий с id={} удален администратором", commId);
    }

    /**
     * Проверка наличия комментария в базе по id
     *
     * @param id Long
     */
    private void commentValidation(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("Комментарий с id = '%s' не найден", id));
        }
    }
}
