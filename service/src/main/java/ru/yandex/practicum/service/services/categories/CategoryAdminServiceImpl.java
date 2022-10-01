package ru.yandex.practicum.service.services.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.CategoryMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;

@Service
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryAdminServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto) {
        categoryValidation(dto.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(dto)));
    }

    @Override
    public CategoryDto postCategory(NewCategoryDto dto) {
        Category category = new Category(0, dto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryValidation(catId);
        if (!eventRepository.findByCategory(catId).isEmpty()) {
            throw new MyNotFoundException(String.format("В базе данных существуют события связанные " +
                    "с категорией с id = '%s'", catId));
        }
        categoryRepository.deleteById(catId);
    }

    private void categoryValidation(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Категория с id = '%s' не найдена", id));
        }
    }
}
