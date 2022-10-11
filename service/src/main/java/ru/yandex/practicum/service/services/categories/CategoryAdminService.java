package ru.yandex.practicum.service.services.categories;

import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto updateCategory(CategoryDto dto);

    CategoryDto postCategory(NewCategoryDto dto);

    void deleteCategory(Long catId);
}
