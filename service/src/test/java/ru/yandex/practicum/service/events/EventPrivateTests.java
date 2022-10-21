package ru.yandex.practicum.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.events.NewEventDto;
import ru.yandex.practicum.service.dto.locations.Location;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class EventPrivateTests {
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
    @Autowired
    private Statistics statistics;
    @Lazy
    @Autowired
    private EventFullMapper fullMapper;
    @Lazy
    @Autowired
    private EventShortMapper shortMapper;
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

    List<EventFullDto> fullDtos = new ArrayList<>();
    List<EventShortDto> shortDtos = new ArrayList<>();


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
    }

    /**
     * тестирование создания события
     * @throws Exception
     */
    @Test
    @Transactional
    public void addEvent() throws Exception{
//        List<ViewStats> views = new ArrayList<>();
//        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
//        views.add(viewStat1);
        //        Mockito
//                .when(statistics.getViewStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
//                .thenReturn(views);

        Location location = new Location(54.1838F, 45.1749F);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Концерт Н.Носкова")
                .category(1L)
                .description("Концерт известного музыканта Н.Носкова")
                .eventDate(createOn.plusDays(2L).format(formatter))
                .location(location)
                .paid(true)
                .participantLimit(1L)
                .requestModeration(false)
                .title("Концерт")
                .commentModeration(true)
                .build();
        assertEquals(shortDtos.size(), eventRepository.findAll().size());

        //работа метода с корректными параметрами
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.annotation", is(newEventDto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(newEventDto.getDescription())))
                .andExpect(jsonPath("$.paid", is(newEventDto.isPaid()), Boolean.class))
                .andExpect(jsonPath("$.participantLimit", is(newEventDto.getParticipantLimit()), Long.class))
                .andExpect(jsonPath("$.requestModeration", is(newEventDto.isRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$.title", is(newEventDto.getTitle())))
                .andExpect(jsonPath("$.commentModeration", is(newEventDto.isCommentModeration()), Boolean.class));

        //работа метода с корректными параметрами
        //несуществующий пользователь
        // некорректная аннотация
        // некорректная категория
        // некорректное описание
        // некорректная дата
        // некорректная локация
        // некорректный заголовок
    }
}
