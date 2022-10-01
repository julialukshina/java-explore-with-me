package ru.yandex.practicum.service.services.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.MyPageable;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.CategoryMapper;
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

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = MyPageable.of(from, size);
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        if (!repository.existsById(catId)) {
            throw new MyNotFoundException(String.format("The category with id = '%s' is not found", catId));
        }
        return CategoryMapper.toCategoryDto(repository.findById(catId).get());
    }
}
