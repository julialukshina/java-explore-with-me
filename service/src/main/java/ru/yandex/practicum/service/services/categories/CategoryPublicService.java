package ru.yandex.practicum.service.services.categories;

import ru.yandex.practicum.service.dto.categories.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);
}
