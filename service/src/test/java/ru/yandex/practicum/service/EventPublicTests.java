package ru.yandex.practicum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.controllers.event.EventPublicController;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;
import ru.yandex.practicum.service.services.events.EventPrivateService;
import ru.yandex.practicum.service.services.events.EventPublicServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class EventPublicTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime createOn = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    Category category1 = new Category(1L, "концерты");
    Category category2 = new Category(2L, "театры");
    Category category3 = new Category(2L, "домашние животные");
    User user = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    Event event1 = new Event(1L,
            "Концерт Н.Носкова",
            category1,
            null,
            createOn,
            "Концерт известного музыканта Н.Носкова",
            createOn.plusDays(2L),
            user,
            true,
            100,
            createOn.plusHours(3L),
            false,
            "Концерт",
            State.PUBLISHED);
    Event event2 = new Event(2L,
            "Спектакль МХАТа",
            category2,
            null,
            createOn.plusHours(1L),
            "Спектакль МХАТа",
            createOn.plusDays(1L),
            user,
            true,
            100,
            createOn.plusHours(5L),
            false,
            "Спектакль",
            State.PENDING);
    Event event3 = new Event(3L,
            "Выставка собак",
            category3,
            null,
            createOn.plusHours(1L),
            "Выставка собак",
            createOn.plusDays(1L),
            user,
            true,
            100,
            createOn.plusHours(5L),
            false,
            "Выставка собак",
            State.PUBLISHED);
    @Autowired
    EventPublicController controller;
    @Autowired
    EventPublicServiceImpl service;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    private String body;

    @BeforeEach //перед каждым тестом в репозиторий добавляются пользователь, категории, события
    public void createItemObject() throws Exception {
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
        userRepository.save(user);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
    }

//    @Transactional
//    @Test
//    public void getById() throws Exception {
//        this.mockMvc.perform(get("/events/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(event1.getId()), Long.class))
//                .andExpect(jsonPath("$.description", is(event1.getDescription())));
//
//
//    }
}
