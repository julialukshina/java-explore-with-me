package ru.yandex.practicum.service.requests;

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
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.requests.RequestMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class RequestPrivateTests {
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
    private CategoryRepository categoryRepository;

    @Autowired
    private RequestRepository requestRepository;
    private List<ParticipationRequestDto> dtos = new ArrayList<>();
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private User user3 = new User(3L, "Иван Смирнов", "smirnov@mail.ru");
    private User user4 = new User(4L, "Иван Васильев", "vasi@mail.ru");
    private Category category = new Category(1L, "концерты");
    private Event event1 = Event.builder()
            .id(1L)
            .annotation("Концерт Н.Носкова")
            .category(category)
            .createdOn(LocalDateTime.now().minusDays(5L))
            .description("Концерт известного музыканта Н.Носкова")
            .eventDate(LocalDateTime.now().minusDays(2L))
            .initiator(user1)
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
            .initiator(user1)
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

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователей, категорию и события в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE requests ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);

        categoryRepository.save(category);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        eventRepository.save(event1);
        eventRepository.save(event2);
    }

    /**
     * тестирование создания заявки
     * @throws Exception
     */
    @Test
    @Transactional
    public void addRequest() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/2/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        //модерация заявок у события отключена
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.requester", is(2L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.CONFIRMED.toString())));
        assertEquals(1, requestRepository.findAll().size());

        //модерация заявок у события включена
        event1.setRequestModeration(true);
        event1.setParticipantLimit(2L);
        eventRepository.save(event1);
        this.mockMvc.perform(post("/users/3/requests?eventId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.event", is(1L), Long.class))
                .andExpect(jsonPath("$.requester", is(3L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.PENDING.toString())));
        assertEquals(2, requestRepository.findAll().size());


        //работа метода с корректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(post("/users/8/requests?eventId=1"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(post("/users/4/requests?eventId=5"))
                .andExpect(status().isNotFound());

        //нельзя добавить заявку повторно
        this.mockMvc.perform(post("/users/2/requests?eventId=1"))
                .andExpect(status().isBadRequest());

        //организатор события не может подать заявку
        this.mockMvc.perform(post("/users/1/requests?eventId=1"))
                .andExpect(status().isBadRequest());

        //событие, на которое подается заявка, не опубликовано
        this.mockMvc.perform(post("/users/2/requests?eventId=2"))
                .andExpect(status().isBadRequest());

        //лимит заявок на событие исчерпан
        event1.setParticipantLimit(1L);
        eventRepository.save(event1);
        this.mockMvc.perform(post("/users/4/requests?eventId=1"))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование выдачи заявок пользователя
     * @throws Exception
     */
    @Test
    @Transactional
    public void getRequests() throws Exception{
        //работа метода с корректными параметрами
        Request request1 = new Request(1L, LocalDateTime.now(), event1, user2, Status.CONFIRMED);
        requestRepository.save(request1);
        Request request2 = new Request(2L, LocalDateTime.now(), event2, user2, Status.PENDING);
        requestRepository.save(request2);
        this.mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        dtos.addAll(Arrays.asList(RequestMapper.toRequestDto(request1),RequestMapper.toRequestDto(request2)));
        this.mockMvc.perform(get("/users/2/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/7/requests"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование закрытия заявки
     * @throws Exception
     */
    @Test
    @Transactional
    public void cancelRequest() throws Exception{
        //работа метода с корректными параметрами
        Request request1 = new Request(1L, LocalDateTime.now(), event1, user2, Status.PENDING);
        requestRepository.save(request1);
        this.mockMvc.perform(patch("/users/2/requests/1/cancel"))
                .andExpect(status().isOk());
        assertTrue(requestRepository.findById(1L).get().getStatus().equals(Status.CANCELED));

        //работа метода с корректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/5/requests/1/cancel"))
                .andExpect(status().isNotFound());

        //несуществующий запрос
        this.mockMvc.perform(patch("/users/2/requests/5/cancel"))
                .andExpect(status().isNotFound());

        // запрос создан другим пользователем
        this.mockMvc.perform(patch("/users/4/requests/1/cancel"))
                .andExpect(status().isBadRequest());


    }
}
