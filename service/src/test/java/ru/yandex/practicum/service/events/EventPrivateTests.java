package ru.yandex.practicum.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.events.NewEventDto;
import ru.yandex.practicum.service.dto.events.UpdateEventRequest;
import ru.yandex.practicum.service.dto.locations.Location;
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.mappers.requests.RequestMapper;
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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private RequestRepository requestRepository;
    @Lazy
    @Autowired
    private EventFullMapper fullMapper;
    @Lazy
    @Autowired
    private EventShortMapper shortMapper;
    @MockBean
    private HitClient client;
    private String body;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
            .state(State.PENDING)
            .commentModeration(true)
            .build();

    List<EventShortDto> shortDtos = new ArrayList<>();
    private List<ParticipationRequestDto> requestDtos = new ArrayList<>();


    @BeforeEach //перед каждым тестом в репозиторий добавляются пользователь, категории, события
    public void createObject() {
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
        userRepository.save(user3);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
    }

    /**
     * тестирование создания события
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addEvent() throws Exception {
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
        assertEquals(1, eventRepository.findAll().size());

        //работа метода с корректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(post("/users/8/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // некорректная аннотация
        newEventDto.setAnnotation("");
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setAnnotation(null);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setAnnotation("test");

        // некорректная категория
        newEventDto.setCategory(5L);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        newEventDto.setCategory(0);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setCategory(1L);

        // некорректное описание
        newEventDto.setDescription("");
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setDescription(null);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setDescription("test");

        // некорректная дата
        newEventDto.setEventDate("");
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setEventDate(null);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setEventDate("test");

        // некорректная локация
        newEventDto.setLocation(null);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setLocation(location);

        // некорректный заголовок
        newEventDto.setTitle("");
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setTitle(null);
        body = objectMapper.writeValueAsString(newEventDto);
        this.mockMvc.perform(post("/users/1/events")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newEventDto.setTitle("test");
    }

    /**
     * тестирование выдачи событий их создателю
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getEventsOfUser() throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        //работа метода с корректными параметрами
        eventRepository.save(event1);
        eventRepository.save(event2);
        EventShortDto dto = shortMapper.toEventShortDto(event1);
        shortDtos.add(dto);
        this.mockMvc.perform(get("/users/1/events"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(shortDtos)));
        this.mockMvc.perform(get("/users/1/events?from=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(shortDtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/5/events"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(get("/users/1/events?from=-1&size=1"))
                .andExpect(status().isInternalServerError());
        this.mockMvc.perform(get("/users/1/events?from=0&size=0"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * параметризованный тест обновления события
     *
     * @param updateDto - dto объект обновления
     */
    @ParameterizedTest
    @ArgumentsSource(UpdateArgumentsProvider.class)
    @Transactional
        public void updateEvent(UpdateEventRequest updateDto) throws Exception {
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));
        eventRepository.save(event1);
        eventRepository.save(event2);

        //работа метода с некорректными параметрами
        //некорректное время
        UpdateEventRequest updateDto1 = UpdateEventRequest.builder()
                .eventId(2L)
                .title("update_title")
                .eventDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        updateDto1.setEventDate(LocalDateTime.now().plusHours(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "HH:mm:ss")));

        //несуществующая категория
        updateDto1.setCategory(8L);
        this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/404/events").content(objectMapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //событие уже опубликовано, поэтому не может быть изменено
        event2.setState(State.PUBLISHED);
        eventRepository.save(event2);
        this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        event2.setState(State.PENDING);
        eventRepository.save(event2);

        //работа метода с корректными параметрами
        if (updateDto.getAnnotation() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.annotation", is(updateDto.getAnnotation()), String.class));
        }
        if (updateDto.getDescription() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.description", is(updateDto.getDescription()), String.class));
        }
        if (updateDto.getTitle() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.title", is(updateDto.getTitle()), String.class));
        }
        if (updateDto.getEventDate() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.eventDate", is(updateDto.getEventDate()), String.class));
        }
        if (updateDto.getPaid() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.paid", is(updateDto.getPaid()), Boolean.class));
        }
        if (updateDto.getParticipantLimit() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.participantLimit", is(updateDto.getParticipantLimit()), Long.class));
        }
        if (updateDto.getCategory() != null) {
            this.mockMvc.perform(patch("/users/2/events").content(objectMapper.writeValueAsString(updateDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2L), Long.class))
                    .andExpect(jsonPath("$.category.id", is(updateDto.getCategory()), Long.class));
        }
    }

    /**
     * тестирование выдачи события по id
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getEvent() throws Exception{
        eventRepository.save(event1);
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        //работа метода с корректными параметрами
        EventFullDto dto = fullMapper.toEventFullDto(event1);
        this.mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.annotation", is(dto.getAnnotation())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.paid", is(dto.isPaid()), Boolean.class))
                .andExpect(jsonPath("$.participantLimit", is(dto.getParticipantLimit()), Long.class))
                .andExpect(jsonPath("$.requestModeration", is(dto.isRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$.title", is(dto.getTitle())))
                .andExpect(jsonPath("$.commentModeration", is(dto.isCommentModeration()), Boolean.class));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(get("/users/5/events/1"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(get("/users/1/events/10"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование закрытия события
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void cancelEvent() throws Exception{
        eventRepository.save(event1);
        eventRepository.save(event2);
        List<ViewStats> views = new ArrayList<>();
        ViewStats viewStat1 = new ViewStats("/events/1", "service", 1L);
        views.add(viewStat1);
        Mockito
                .when(client.getStats(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean()))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(views), HttpStatus.OK));

        //работа метода с корректными параметрами
        this.mockMvc.perform(patch("/users/2/events/2"))
                .andExpect(status().isOk());

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/5/events/2"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(patch("/users/1/events/20"))
                .andExpect(status().isNotFound());

        //пользователь не является организатором события
        this.mockMvc.perform(patch("/users/1/events/2"))
                .andExpect(status().isBadRequest());

        //событие уже опубликовано и не может быть закрыто
        this.mockMvc.perform(patch("/users/1/events/1"))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование выдачи запросов на участие в событии, созданном пользователем
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void getRequests() throws Exception{
        eventRepository.save(event1);
        eventRepository.save(event2);
        Request request1 = new Request(1L, LocalDateTime.now(), event2, user1, Status.CONFIRMED);
        requestRepository.save(request1);
        Request request2 = new Request(2L, LocalDateTime.now(), event2, user3, Status.CONFIRMED);
        requestRepository.save(request2);
        requestDtos.addAll(Arrays.asList(RequestMapper.toRequestDto(request1), RequestMapper.toRequestDto(request2)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/2/events/2/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtos)));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(get("/users/5/events/2/requests"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(get("/users/2/events/5/requests"))
                .andExpect(status().isNotFound());

        //пользователь не является организатором события
        this.mockMvc.perform(get("/users/1/events/2/requests"))
                .andExpect(status().isBadRequest());

    }

    /**
     * тестирование одобрения запроса на участие в событии
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void confirmRequest() throws Exception{
        eventRepository.save(event1);
        eventRepository.save(event2);
        Request request = new Request(1L, LocalDateTime.now(), event2, user1, Status.PENDING);
        requestRepository.save(request);
        ParticipationRequestDto dto = RequestMapper.toRequestDto(request);
        dto.setStatus(Status.CONFIRMED.toString());
        System.out.println("\n");
        System.out.println("\n");
        System.out.println(requestRepository.findAll());
        System.out.println("\n");
        System.out.println("\n");
        //работа метода с корректными параметрами
        this.mockMvc.perform(patch("/users/2/events/2/requests/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/5/events/2/requests/1/confirm"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(patch("/users/2/events/5/requests/1/confirm"))
                .andExpect(status().isNotFound());

        //несуществующий запрос
        this.mockMvc.perform(patch("/users/2/events/2/requests/5/confirm"))
                .andExpect(status().isNotFound());

        //пользователь не является организатором события
        this.mockMvc.perform(patch("/users/1/events/2/requests/1/confirm"))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование отклонения запроса на участие в событии
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectRequest() throws Exception{
        eventRepository.save(event1);
        eventRepository.save(event2);
        Request request = new Request(1L, LocalDateTime.now(), event2, user1, Status.PENDING);
        requestRepository.save(request);
        ParticipationRequestDto dto = RequestMapper.toRequestDto(request);
        dto.setStatus(Status.REJECTED.toString());

        //работа метода с корректными параметрами
        this.mockMvc.perform(patch("/users/2/events/2/requests/1/reject"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/5/events/2/requests/1/reject"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(patch("/users/2/events/5/requests/1/reject"))
                .andExpect(status().isNotFound());

        //несуществующий запрос
        this.mockMvc.perform(patch("/users/2/events/2/requests/5/reject"))
                .andExpect(status().isNotFound());

        //пользователь не является организатором события
        this.mockMvc.perform(patch("/users/1/events/2/requests/1/reject"))
                .andExpect(status().isBadRequest());
    }

    static class UpdateArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).annotation("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).description("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).title("update").build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).
                            eventDate(LocalDateTime.now().plusDays(5)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).paid(true).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).participantLimit(50L).build()),
                    Arguments.of(UpdateEventRequest.builder().eventId(2L).category(1L).build())
            );
        }
    }
}
