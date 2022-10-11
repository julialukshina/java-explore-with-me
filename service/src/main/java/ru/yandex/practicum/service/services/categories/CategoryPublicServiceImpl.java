package ru.yandex.practicum.service.services.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Pageable;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.repositories.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {

    private final CategoryRepository repository;

    @Autowired
    public CategoryPublicServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Выдача списка категорий
     *
     * @param from int
     * @param size int
     * @return List<CategoryDto>
     */
    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        org.springframework.data.domain.Pageable pageable = Pageable.of(from, size);
        List<CategoryDto> categoryDtos = repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("Выдан список категорий");
        return categoryDtos;
    }

    /**
     * Выдача категории по id
     *
     * @param catId Long
     * @return CategoryDto
     */
    @Override
    public CategoryDto getCategoryById(Long catId) {
        if (!repository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id = '%s' не найдена", catId));
        }
        CategoryDto dto = CategoryMapper.toCategoryDto(repository.findById(catId).get());
        log.info("Предоставлена категория с id = {}", catId);
        return dto;
    }
}
