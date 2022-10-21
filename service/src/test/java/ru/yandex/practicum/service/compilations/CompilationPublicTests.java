package ru.yandex.practicum.service.compilations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.mappers.compilations.CompilationMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;
import ru.yandex.practicum.service.services.events.EventPublicService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class CompilationPublicTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CompilationRepository compilationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Lazy
    @Autowired
    private CompilationMapper mapper;
    private List<CompilationDto> dtos = new ArrayList<>();

    private User user = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private Category category = new Category(1L, "концерты");
    private Event event1 = Event.builder()
            .id(1L)
            .annotation("Концерт Н.Носкова")
            .category(category)
            .createdOn(LocalDateTime.now().minusDays(5L))
            .description("Концерт известного музыканта Н.Носкова")
            .eventDate(LocalDateTime.now().minusDays(2L))
            .initiator(user)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(0)
            .publishedOn(LocalDateTime.now().minusDays(4L))
            .requestModeration(false)
            .title("Концерт")
            .state(State.PUBLISHED)
            .commentModeration(true)
            .build();
    private Event event2 = Event.builder()
            .id(2L)
            .annotation("Спектакль МХАТа")
            .category(category)
            .createdOn(LocalDateTime.now().minusDays(3L))
            .description("Спектакль МХАТа")
            .eventDate(LocalDateTime.now().minusDays(1L))
            .initiator(user)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(1L)
            .publishedOn(LocalDateTime.now().minusDays(2L))
            .requestModeration(false)
            .title("Спектакль")
            .state(State.PENDING)
            .commentModeration(true)
            .build();

    private Compilation compilation1 = new Compilation(1L, new HashSet<>(), false, "Самое интересное на неделе");
    private Compilation compilation2 = new Compilation(2L, new HashSet<>(), true, "Самое интересное за месяц");

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователя и обращение в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE compilations ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);

        categoryRepository.save(category);
        userRepository.save(user);
        eventRepository.save(event1);
        eventRepository.save(event2);
        compilationRepository.save(compilation1);
        compilationRepository.save(compilation2);
    }

    /**
     * тестирование выдачи списка подборок
     * @throws Exception
     */
    @Test
    @Transactional
    public void getCompilations() throws Exception{
        //корректная работа метода без параметров
        dtos.addAll(Arrays.asList(mapper.toCompilationDto(compilation1), mapper.toCompilationDto(compilation2)));
        this.mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        dtos.remove(1);
        this.mockMvc.perform(get("/compilations?from=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        this.mockMvc.perform(get("/compilations?pinned=false&from=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/compilations?from=-1&size=0"))
                .andExpect(status().isInternalServerError());
        //некорректный from
        this.mockMvc.perform(get("/compilations?from=-1&size=1"))
                .andExpect(status().isInternalServerError());
        //некорректный size
        this.mockMvc.perform(get("/compilations?from=0&size=0"))
                .andExpect(status().isInternalServerError());
    }
    /**
     * тестирование выдачи подборки по id
     * @throws Exception
     */
    @Test
    @Transactional
    public void getCompilationById() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toCompilationDto(compilation1))));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/compilations/5"))
                .andExpect(status().isNotFound());
    }
}
