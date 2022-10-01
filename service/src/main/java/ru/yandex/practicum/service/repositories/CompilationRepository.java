package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
