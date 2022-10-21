package ru.yandex.practicum.service.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.categories.CategoryDto;
import ru.yandex.practicum.service.dto.categories.NewCategoryDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class CategoryAdminTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    private Category category1 = new Category(1L, "концерты");
    private Category category2 = new Category(2L, "театры");
    private List<CategoryDto> dtos = new ArrayList<>();
    private String body;

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем матегорию в репозиторий
    public void createObject(){
        String sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        categoryRepository.save(category1);
        dtos.add(CategoryMapper.toCategoryDto(category1));
    }

    /**
     * тестирование создания категории
     * @throws Exception
     */
    @Test
    @Transactional
    public void postCategory() throws Exception {
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        NewCategoryDto dto = new NewCategoryDto("театры");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(category2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(category2.getName())));
        dtos.add(CategoryMapper.toCategoryDto(category2));
        this.mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        //категория должна быть уникальной
        this.mockMvc.perform(post("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        dto.setName("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        dto = null;
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Тестирование обновления категории
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateCategory() throws Exception{
        //работа метода с корректными параметрами
        CategoryDto dto = new CategoryDto(1L, "test");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())));

        //работа метода с некорректными параметрами
        dto.setId(6);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        dto.setId(-5);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setId(1);
        dto.setName(null);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setName("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/admin/categories")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тестирование удаления категории
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteCategory() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        dtos.remove(0);
        System.out.println(categoryRepository.findAll());
        this.mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isOk());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(delete("/admin/categories/10"))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete("/admin/categories/-1"))
                .andExpect(status().isInternalServerError());

        //работа метода в случае, если существует событие с такой категорией
        categoryRepository.save(category2);
        User user = new User(1L, "Иван Иванов", "ivanov@mail.ru");
        userRepository.save(user);
        Event event = Event.builder()
                .id(1L)
                .annotation("Концерт Н.Носкова")
                .category(category2)
                .createdOn(LocalDateTime.now())
                .description("Концерт известного музыканта Н.Носкова")
                .eventDate(LocalDateTime.now().plusDays(2L))
                .initiator(user)
                .lat(54.1838F)
                .lon(45.1749F)
                .paid(true)
                .participantLimit(100L)
                .publishedOn(LocalDateTime.now().plusHours(3L))
                .requestModeration(false)
                .title("Концерт")
                .state(State.PUBLISHED)
                .commentModeration(true)
                .build();
        eventRepository.save(event);
        this.mockMvc.perform(delete("/admin/categories/2"))
                .andExpect(status().isConflict());
    }
}
