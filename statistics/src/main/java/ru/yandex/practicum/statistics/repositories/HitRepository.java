package ru.yandex.practicum.statistics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.statistics.model.Hit;

import java.time.LocalDateTime;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query(value = "SELECT COUNT(id) FROM Hits WHERE timestamp>=?1 AND timestamp<=?2 AND uri LIKE ?3", nativeQuery = true)
    Long getStatistics(LocalDateTime start, LocalDateTime end, String uri);

    @Query(value = "SELECT COUNT(id) FROM Hits WHERE timestamp>=?1 AND timestamp<=?2 AND uri LIKE ?3 ORDER BY ip", nativeQuery = true)
    Long getUniqueStatistics(LocalDateTime start, LocalDateTime end, String uri);

}
