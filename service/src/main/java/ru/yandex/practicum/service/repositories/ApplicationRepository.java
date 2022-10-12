package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppStatus;
import ru.yandex.practicum.service.models.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByAuthorId(Long authorId, Pageable pageable);

    Page<Application> findAllByAppReasonOrderByCreatedAsc(AppReason reason, Pageable pageable);

    Page<Application> findAllByAppStatusOrderByCreatedAsc(AppStatus status, Pageable pageable);
}
