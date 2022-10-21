package ru.yandex.practicum.service.compilations;

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
import ru.yandex.practicum.service.dto.compilations.CompilationDto;
import ru.yandex.practicum.service.dto.compilations.NewCompilationDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Compilation;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.CompilationRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
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
public class CompilationAdminTests {
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
    private List<CompilationDto> dtos = new ArrayList<>();
    private String body;
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

    private Compilation compilation = new Compilation(1L, new HashSet<>(), false, "Самое интересное на неделе");

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
    }

    /**
     * тестирование создания подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void addCompilations() throws Exception{
        //работа метода с корректными параметрами
        //пустой лист событий
        this.mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        List<Long> events = new ArrayList<>();
        NewCompilationDto dto = new NewCompilationDto(events, false, "Самое интересное на неделе");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/compilations")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.title", is(dto.getTitle())));
        assertEquals(1, compilationRepository.findAll().size());

        //подборка с событиями
        events.add(1L);
        events.add(2L);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/compilations")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.title", is(dto.getTitle())));
        assertEquals(2, compilationRepository.findAll().size());

        // лист событий равен null
        dto.setEvents(null);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/compilations")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.title", is(dto.getTitle())));
        assertEquals(3, compilationRepository.findAll().size());

        //работа метода с корректными параметрами
        //несуществующие события в подборке
        events.add(8L);
        dto.setEvents(events);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/admin/compilations")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование удаления подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteCompilations() throws Exception{
        //работа метода с корректными параметрами
        compilationRepository.save(compilation);
        assertEquals(1, compilationRepository.findAll().size());
        this.mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isOk());
        assertEquals(0, compilationRepository.findAll().size());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование удаления события из подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteEventFromCompilation() throws Exception{
        //работа метода с корректными параметрами
        HashSet<Event> events= new HashSet<>();
        events.add(event1);
        events.add(event2);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        assertEquals(2, compilationRepository.findById(1L).get().getEvents().size());
        this.mockMvc.perform(delete("/admin/compilations/1/events/1"))
                .andExpect(status().isOk());
        assertEquals(1, compilationRepository.findById(1L).get().getEvents().size());

        //работа метода с некорректными параметрами
        //подборки не существует в базе
        this.mockMvc.perform(delete("/admin/compilations/2/events/2"))
                .andExpect(status().isNotFound());

        //события не существует в базе
        this.mockMvc.perform(delete("/admin/compilations/1/events/6"))
                .andExpect(status().isNotFound());

        //события не существует в подборке
        this.mockMvc.perform(delete("/admin/compilations/1/events/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование удаления события из подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void addEventFromCompilation() throws Exception{
        //работа метода с корректными параметрами
        compilationRepository.save(compilation);
        assertEquals(0, compilationRepository.findById(1L).get().getEvents().size());
        this.mockMvc.perform(patch("/admin/compilations/1/events/1"))
                .andExpect(status().isOk());
        assertEquals(1, compilationRepository.findById(1L).get().getEvents().size());
        //работа метода с некорректными параметрами
        //подборки не существует в базе
        this.mockMvc.perform(patch("/admin/compilations/2/events/1"))
                .andExpect(status().isNotFound());

        //события не существует в базе
        this.mockMvc.perform(patch("/admin/compilations/1/events/6"))
                .andExpect(status().isNotFound());

        //события уже существует в подборке
        this.mockMvc.perform(patch("/admin/compilations/1/events/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование открепления подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void unpinCompilation() throws Exception{
        //работа метода с корректными параметрами
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        this.mockMvc.perform(delete("/admin/compilations/1/pin"))
                .andExpect(status().isOk());
        assertFalse(compilationRepository.findById(1L).get().isPinned());

        //работа метода с некорректными параметрами
        //подборки не существует в базе
        this.mockMvc.perform(delete("/admin/compilations/5/pin"))
                .andExpect(status().isNotFound());

        //подборки уже откреплена
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        this.mockMvc.perform(delete("/admin/compilations/1/pin"))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование закрепления подборки
     * @throws Exception
     */
    @Test
    @Transactional
    public void pinCompilation() throws Exception{
        //работа метода с корректными параметрами
        compilationRepository.save(compilation);
        this.mockMvc.perform(patch("/admin/compilations/1/pin"))
                .andExpect(status().isOk());
        assertTrue(compilationRepository.findById(1L).get().isPinned());

        //работа метода с некорректными параметрами
        //подборки не существует в базе
        this.mockMvc.perform(patch("/admin/compilations/5/pin"))
                .andExpect(status().isNotFound());

        //подборки уже откреплена
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        this.mockMvc.perform(patch("/admin/compilations/1/pin"))
                .andExpect(status().isBadRequest());
    }
}
