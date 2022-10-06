package ru.yandex.practicum.service.controllers.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

//    @PostMapping("/admin/categories")
//    public CategoryDto postCategory(@RequestBody String s) {
//        String[] a = s.split("\"");
//        String b = a[3];
//        NewCategoryDto dto = new NewCategoryDto(b);
//        System.out.println("зашёл в ручку");
//        return service.postCategory(dto);
//    }


    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategory(catId);
    }
}
