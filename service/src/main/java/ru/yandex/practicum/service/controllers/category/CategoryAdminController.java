package ru.yandex.practicum.service.controllers.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.services.categories.CategoryAdminService;

@RestController
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryAdminService service;

    @Autowired
    public CategoryAdminController(CategoryAdminService service) {
        this.service = service;
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto) {
        return service.updateCategory(dto);
    }

    @PostMapping("/admin/categories")
    public CategoryDto postCategory(@RequestBody NewCategoryDto dto) {
        System.out.println(dto);
        return service.postCategory(dto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
    }
}
