package ru.yandex.practicum.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class EventPublicTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private String body;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime createOn = LocalDateTime.now();
    private Category category1 = new Category(1L, "концерты");
    private Category category2 = new Category(2L, "театры");
    private Category category3 = new Category(2L, "домашние животные");
    private User user = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private Event event1 = Event.builder()
            .id(1L)
            .annotation("Концерт Н.Носкова")
            .category(category1)
            .createdOn(createOn)
            .description("Концерт известного музыканта Н.Носкова")
            .eventDate(createOn.plusDays(2L))
            .initiator(user)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(100L)
            .publishedOn(createOn.plusHours(3L))
            .requestModeration(false)
            .title("Концерт")
            .state(State.PUBLISHED)
            .commentModeration(true)
            .build();
    private Event event2 = Event.builder()
            .id(2L)
            .annotation("Спектакль МХАТа")
            .category(category2)
            .createdOn(createOn.plusHours(1L))
            .description("Спектакль МХАТа")
            .eventDate(createOn.plusDays(1L))
            .initiator(user)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(100L)
            .publishedOn(createOn.plusHours(2L))
            .requestModeration(false)
            .title("Спектакль")
            .state(State.PENDING)
            .commentModeration(true)
            .build();

    private Event event3 = Event.builder()
            .id(3L)
            .annotation("Выставка собак")
            .category(category3)
            .createdOn(createOn.plusHours(2L))
            .description("Выставка собак")
            .eventDate(createOn.plusDays(1L))
            .initiator(user)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(0)
            .publishedOn(createOn.plusHours(4L))
            .requestModeration(false)
            .title("Выставка собак")
            .state(State.PENDING)
            .commentModeration(true)
            .build();


    @BeforeEach //перед каждым тестом в репозиторий добавляются пользователь, категории, события
    public void createObject(){
        String sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1"; //скидываем счетчики
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE compilations ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
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
////        this.mockMvc.perform(get("/events/1"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.id", is(event1.getId()), Long.class))
////                .andExpect(jsonPath("$.description", is(event1.getDescription())));
////        System.out.println(categoryRepository.findById(1L).get());
//        NewCategoryDto dto = new NewCategoryDto("ee");
//        body=objectMapper.writeValueAsString(dto);
//        this.mockMvc.perform(post("/admin/categories").content(body).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
}