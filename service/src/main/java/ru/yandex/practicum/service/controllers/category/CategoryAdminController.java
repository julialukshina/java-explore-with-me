package ru.yandex.practicum.service.controllers.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.services.categories.CategoryAdminService;

@RestController
@RequestMapping("/admin")
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryAdminService service;

    @Autowired
    public CategoryAdminController(CategoryAdminService service) {
        this.service = service;
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto) {
        return service.updateCategory(dto);
    }

    @PostMapping("/categories")
    public CategoryDto postCategory(@RequestBody NewCategoryDto dto) {
        return service.postCategory(dto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
    }
}
