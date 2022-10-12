package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByEventId(Long eventId, Pageable pageable);
}
