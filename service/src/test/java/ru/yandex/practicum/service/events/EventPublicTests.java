package ru.yandex.practicum.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.RequestRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Autowired
    private RequestRepository requestRepository;

    @MockBean
    private HitClient client;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime createOn = LocalDateTime.now();
    private Category category1 = new Category(1L, "концерты");
    private Category category2 = new Category(2L, "театры");
    private Category category3 = new Category(3L, "домашние животные");
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private Event event1 = Event.builder()
            .id(1L)
            .annotation("Концерт Н.Носкова")
            .category(category1)
            .createdOn(createOn)
            .description("Концерт известного музыканта Н.Носкова")
            .eventDate(createOn.plusDays(2L))
            .initiator(user1)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(1L)
            .publishedOn(createOn.plusHours(3L))
            .requestModeration(false)
            .title("Концерт")
            .state(State.PUBLISHED)
            .commentModeration(true)
            .build();
    private Event event2 = Event.builder()
            .id(2L)
            .annotation("Спектакль МХАТа test")
            .category(category2)
            .createdOn(createOn.plusHours(1L))
            .description("Спектакль МХАТа")
            .eventDate(createOn.plusDays(1L))
            .initiator(user1)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(1L)
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
            .eventDate(createOn.plusDays(5L))
            .initiator(user1)
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
        sqlQuery = "ALTER TABLE requests ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        jdbcTemplate.update(sqlQuery);
        userRepository.save(user1);
        userRepository.save(user2);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
    }

    /**
     * параметризованный запрос списка событий
     *
     * @param argument - объект класса для передачи параметров запроса в тест
     */
    @ParameterizedTest
    @ArgumentsSource(FindArgumentsProvider.class)
    @Transactional
    void getEvents(FindPublicArgument argument) throws Exception {
        this.mockMvc.perform(get("/events?sort=FALSE"))
                .andExpect(status().isInternalServerError());
        this.mockMvc.perform(get("/events?rangeStart=10.15.2015"))
                .andExpect(status().isInternalServerError());

        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));



        StringBuilder sb = new StringBuilder();
        sb.append("/events");
        if (argument.getCategories() != null) {
            sb.append("?categories=");
            sb.append(argument.getCategories().get(0));
            argument.getCategories().stream()
                    .skip(1)
                    .forEach(c -> sb.append(",").append(c));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
        if (argument.getPaid() != null) {
            sb.append("?paid=");
            sb.append(argument.getPaid());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[2].id", is(3L), Long.class));
        }
        if (argument.getRangeStart() != null) {
            sb.append("?rangeStart=");
            sb.append(argument.getRangeStart());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(3L), Long.class));
        }
        if (argument.getRangeEnd() != null) {
            sb.append("?rangeEnd=");
            sb.append(argument.getRangeEnd());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[2].id", is(3L), Long.class));
        }
        if (argument.getText() != null) {
            sb.append("?text=");
            sb.append(argument.getText());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class));
        }
        if (argument.getSort() != null) {
            sb.append("?sort=");
            sb.append(argument.getSort());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[2].id", is(3L), Long.class));
        }
        if (argument.getOnlyAvailable() != null) {
            Request request1 = new Request(1L, LocalDateTime.now(), event1, user2, Status.CONFIRMED);
            requestRepository.save(request1);
            sb.append("?onlyAvailable=");
            sb.append(argument.getOnlyAvailable());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(3L), Long.class));
        }
    }

    /**
     * поиск события по id
     */
    @Test
    @Transactional
    void getById() throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        //работа меотода с корректрными параметрами
        this.mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(event1.getId()), Long.class))
                .andExpect(jsonPath("$.initiator.id", is(event1.getInitiator().getId()), Long.class))
                .andExpect(jsonPath("$.state", is("PUBLISHED"), String.class))
                .andExpect(jsonPath("$.title", is(event1.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(event1.getAnnotation()), String.class))
                .andExpect(jsonPath("$.description", is(event1.getDescription()), String.class));

        //работа меотода с некорректрными параметрами
        //событие не опубликовано
        this.mockMvc.perform(get("/events/2"))
                .andExpect(status().isBadRequest());

        //событие не существует
        this.mockMvc.perform(get("/events/404"))
                .andExpect(status().isNotFound());
    }
    /**
     * класс для создания объектов параметров запроса для теста запроса событий
     */
    static class FindArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(FindPublicArgument.builder().text("TeSt").build()),
                    Arguments.of(FindPublicArgument.builder().paid(true).build()),
                    Arguments.of(FindPublicArgument.builder().categories(Arrays.asList(1L, 2L)).build()),
                    Arguments.of(FindPublicArgument.builder().rangeStart(LocalDateTime.now().plusDays(4)
                            .format(formatter)).build()),
                    Arguments.of(FindPublicArgument.builder().rangeEnd(LocalDateTime.now().plusDays(14)
                            .format(formatter)).build()),
                    Arguments.of(FindPublicArgument.builder().onlyAvailable(true).build()),
                    Arguments.of(FindPublicArgument.builder().sort(Sort.EVENT_DATE).build())
            );
        }
    }
}