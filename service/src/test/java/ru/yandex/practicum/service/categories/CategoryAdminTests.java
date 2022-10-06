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
import ru.yandex.practicum.service.controllers.category.CategoryAdminController;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.services.categories.CategoryAdminServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class CategoryAdminTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    CategoryAdminController categoryAdminController;
    @Autowired
    CategoryAdminServiceImpl service;
    @Autowired
    CategoryRepository repository;
    String body;

    @BeforeEach //перед каждым тестом в репозиторий добавляются пользователь, категории, события
    public void createObject() throws Exception {
        String sqlQuery = "ALTER TABLE Categories ALTER COLUMN id RESTART WITH 1"; //скидываем счетчики
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE Compilations ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE Users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE Events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE Requests ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE Comments ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    @Transactional
    @Test
    public void addCategory() throws Exception{
        NewCategoryDto dto = new NewCategoryDto("домашние животные");
        body = objectMapper.writeValueAsString(dto);
//        categoryAdminController.postCategory(dto);
//        System.out.println(categoryAdminController.postCategory(dto));
//        System.out.println(repository.findById(1L));
//         CategoryDto categoryDto = new CategoryDto(1L, "new");
//        body = objectMapper.writeValueAsString(categoryDto);
//                this.mockMvc.perform(patch("/admin/categories")
//                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//        System.out.println(repository.findById(1L));
//        this.mockMvc.perform(post("/admin/categories")
//                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//        NewUserRequest request = new NewUserRequest("ar@yandex.ru", "Re");
//        body = objectMapper.writeValueAsString(request);
//                this.mockMvc.perform(post("/admin/users")
//                .content(body).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
