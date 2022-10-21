package ru.yandex.practicum.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.service.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
