package ru.yandex.practicum.service.mappers;

import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.models.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategory(CategoryDto dto) {
        return new Category(dto.getId(), dto.getName());
    }
}
