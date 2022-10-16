package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByEventId(Long eventId, Pageable pageable);
}
