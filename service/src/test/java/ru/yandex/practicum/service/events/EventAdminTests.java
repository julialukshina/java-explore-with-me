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
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.AdminUpdateEventRequest;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.events.FindAdminArgument;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class EventAdminTests {
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
    @Lazy
    @Autowired
    private EventFullMapper fullMapper;
    @MockBean
    private HitClient client;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime createOn = LocalDateTime.now();
    private Category category1 = new Category(1L, "концерты");
    private Category category2 = new Category(2L, "театры");
    private Category category3 = new Category(3L, "домашние животные");
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private User user3 = new User(3L, "Иван Смирнов", "smirnov@mail.ru");
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
            .initiator(user2)
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
            .initiator(user1)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(0)
            .publishedOn(createOn.plusHours(4L))
            .requestModeration(false)
            .title("Выставка собак")
            .state(State.REJECTED)
            .commentModeration(true)
            .build();


    @BeforeEach //перед каждым тестом в репозиторий добавляются пользователь, категории, события
    public void createObject() {
        String sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1"; //скидываем счетчики
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        jdbcTemplate.update(sqlQuery);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
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
     * @param argument - объект класса для передачи параметров поиска в тест
     */
    @ParameterizedTest
    @ArgumentsSource(FindArgumentsProvider.class)
    @Transactional
    void getEvents(FindAdminArgument argument) throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        StringBuilder sb = new StringBuilder();
        sb.append("/admin/events");
        if (argument.getUsers() != null) {
            sb.append("?users=");
            sb.append(argument.getUsers().get(0));
            argument.getUsers().stream()
                    .skip(1)
                    .forEach(u -> sb.append(",").append(u));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(3L), Long.class))
                    .andExpect(jsonPath("$[2].id", is(2L), Long.class));
        }
        if (argument.getStates() != null) {
            sb.append("?states=");
            sb.append(argument.getStates().get(0));
            argument.getStates().stream()
                    .skip(1)
                    .forEach(s -> sb.append(",").append(s));
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                    .andExpect(jsonPath("$[1].id", is(2L), Long.class));
        }
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
        if (argument.getRangeStart() != null) {
            sb.append("?rangeStart=");
            sb.append(argument.getRangeStart());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }
        if (argument.getRangeEnd() != null) {
            sb.append("?rangeEnd=");
            sb.append(argument.getRangeEnd());
            this.mockMvc.perform(get(sb.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

        }
    }

    /**
     * обновление события
     *
     * @param updateDto - dto объект для обновления события
     */
    @ParameterizedTest
    @ArgumentsSource(UpdateArgumentsProvider.class)
    @Transactional
    void updateEvent(AdminUpdateEventRequest updateDto) throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        this.mockMvc.perform(put("/admin/events/404").content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        if (updateDto.getAnnotation() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.annotation", is(updateDto.getAnnotation()), String.class));
        }
        if (updateDto.getDescription() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.description", is(updateDto.getDescription()), String.class));
        }
        if (updateDto.getTitle() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.title", is(updateDto.getTitle()), String.class));
        }
        if (updateDto.getEventDate() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.eventDate", is(updateDto.getEventDate()), String.class));
        }
        if (updateDto.getPaid() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.paid", is(updateDto.getPaid()), Boolean.class));
        }
        if (updateDto.getParticipantLimit() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.participantLimit", is(updateDto.getParticipantLimit()), Long.class));
        }
        if (updateDto.getCategory() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.category.id", is(updateDto.getCategory()), Long.class));
        }
        if (updateDto.getRequestModeration() != null) {
            this.mockMvc.perform(put("/admin/events/1").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1L), Long.class))
                    .andExpect(jsonPath("$.requestModeration", is(updateDto.getRequestModeration()), Boolean.class));
        }
    }

    /**
     * тестирование выдачи публикации события администратором
     *
     * @throws Exception
     */
    @Test
    @Transactional
        void publishEvent () throws Exception {
        event1.setState(State.PENDING);
        eventRepository.save(event1);
        event1.setState(State.PUBLISHED);
        EventFullDto dto = fullMapper.toEventFullDto(event1);
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        //работа метода с корректными параметрами
        this.mockMvc.perform(patch("/admin/events/1/publish"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        //работа метода с некорректными параметрами
        //несуществующее событие
        this.mockMvc.perform(patch("/admin/events/10/publish"))
                .andExpect(status().isNotFound());

        //событие уже опубликовано
        this.mockMvc.perform(patch("/admin/events/1/publish"))
                .andExpect(status().isBadRequest());
        }

        /**
         * тестирование выдачи отклонения события администратором
         *
         * @throws Exception
         */
        @Test
        @Transactional
        void rejectEvent () throws Exception {
            event1.setState(State.PENDING);
            eventRepository.save(event1);
            event1.setState(State.CANCELED);
            EventFullDto dto = fullMapper.toEventFullDto(event1);
            List<ViewStats> views = new ArrayList<>();
            ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
            views.add(viewStat1);
            Mockito
                    .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                    .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

            //работа метода с корректными параметрами
            this.mockMvc.perform(patch("/admin/events/1/reject"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(dto)));

            //работа метода с некорректными параметрами
            //несуществующее событие
            this.mockMvc.perform(patch("/admin/events/10/reject"))
                    .andExpect(status().isNotFound());

            //событие уже опубликовано
            event1.setState(State.PUBLISHED);
            eventRepository.save(event1);
            this.mockMvc.perform(patch("/admin/events/1/reject"))
                    .andExpect(status().isBadRequest());
        }

        /**
         * класс для создания параметров к тесту обновления события
         */
        static class UpdateArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                        Arguments.of(AdminUpdateEventRequest.builder().annotation("update").build()),
                        Arguments.of(AdminUpdateEventRequest.builder().description("update").build()),
                        Arguments.of(AdminUpdateEventRequest.builder().title("update").build()),
                        Arguments.of(AdminUpdateEventRequest.builder().
                                eventDate(LocalDateTime.now().plusDays(5)
                                        .format(formatter)).build()),
                        Arguments.of(AdminUpdateEventRequest.builder().paid(true).build()),
                        Arguments.of(AdminUpdateEventRequest.builder().participantLimit(50L).build()),
                        Arguments.of(AdminUpdateEventRequest.builder().category(2L).build()),
                        Arguments.of(AdminUpdateEventRequest.builder().requestModeration(true).build())
                );
            }
        }

        /**
         * класс для создания параметров к тесту запроса списка событий
         */
        static class FindArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                        Arguments.of(FindAdminArgument.builder().users(Arrays.asList(1L, 2L)).build()),
                        Arguments.of(FindAdminArgument.builder().states(Arrays.asList(State.PENDING, State.PUBLISHED))
                                .build()),
                        Arguments.of(FindAdminArgument.builder().categories(Arrays.asList(1L, 2L)).build()),
                        Arguments.of(FindAdminArgument.builder().rangeStart(LocalDateTime.now().minusDays(1)
                                .format(formatter)).build()),
                        Arguments.of(FindAdminArgument.builder().rangeEnd(LocalDateTime.now().plusDays(10)
                                .format(formatter)).build())
                );
            }
        }
    }
