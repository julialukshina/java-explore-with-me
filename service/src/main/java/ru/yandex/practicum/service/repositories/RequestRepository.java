package ru.yandex.practicum.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEvent(Long eventId);

    List<Request> findByRequester(Long requesterId);

    Request findByEventAndRequester(Long eventId, Long requesterId);
}
