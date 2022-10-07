package ru.yandex.practicum.service.services.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;


@Service
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryAdminServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Обновление категории
     *
     * @param dto CategoryDto
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto dto) {
        categoryValidation(dto.getId());
        log.info("Категория с id {} обновлена", dto.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(dto)));
    }

    /**
     * Создание категории
     *
     * @param dto NewCategoryDto
     * @return CategoryDto
     */
    @Override
    @Transactional
    public CategoryDto postCategory(NewCategoryDto dto) {
        Category category = categoryRepository.save(new Category(0, dto.getName()));
        log.info("Создана новая категория {}", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    /**
     * Удаление категории по id
     *
     * @param catId Long
     */

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryValidation(catId);
        if (!eventRepository.findByCategoryId(catId).isEmpty()) {
            throw new MyNotFoundException(String.format("В базе данных существуют события связанные " +
                    "с категорией с id = '%s'", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория с id {} удалена", catId);
    }

    /**
     * Проверка категории на наличие в базе по id
     *
     * @param id Long
     */
    private void categoryValidation(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Категория с id = '%s' не найдена", id));
        }
    }
}
