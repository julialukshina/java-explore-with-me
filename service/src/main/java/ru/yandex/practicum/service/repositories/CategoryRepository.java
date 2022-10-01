package ru.yandex.practicum.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
