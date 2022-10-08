package ru.yandex.practicum.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.models.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventId(Long eventId);

    List<Request> findByRequesterId(Long requesterId);

    Request findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Integer countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findByEventIdAndStatus(Long eventId, Status status);

    Boolean existsByEventIdAndStatus(Long eventId, Status status);
}
