package ru.yandex.practicum.service.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class CategoryPublicTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CategoryRepository categoryRepository;
    private Category category1 = new Category(1L, "концерты");
    private Category category2 = new Category(2L, "театры");
    private List <CategoryDto> dtos = new ArrayList<>();

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем матегории в репозиторий
    public void createObject() {
        String sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
    }

    /**
     * тест на выдачу всех категорий
     * @throws Exception
     */
    @Transactional
    @Test
    public void getCategory() throws Exception{
        dtos.addAll(Arrays.asList(CategoryMapper.toCategoryDto(category1), CategoryMapper.toCategoryDto(category2)));
        //корректная работа метода без параметров
        this.mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        dtos.remove(1);
        this.mockMvc.perform(get("/categories?from=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/categories?from=-1&size=0"))
                .andExpect(status().isInternalServerError());
        //некорректный from
        this.mockMvc.perform(get("/categories?from=-1&size=1"))
                .andExpect(status().isInternalServerError());
        //некорректный size
        this.mockMvc.perform(get("/categories?from=0&size=0"))
                .andExpect(status().isInternalServerError());
    }
    /**
     * тест на выдачу категории по id
     * @throws Exception
     */
    @Transactional
    @Test
    public void getCategoryById() throws Exception{
        dtos.add(CategoryMapper.toCategoryDto(category1));
        //корректная работа метода
        this.mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos.get(0))));

        //работа метода с некорректным параметром
        this.mockMvc.perform(get("/categories/10"))
                .andExpect(status().isNotFound());
    }

}
