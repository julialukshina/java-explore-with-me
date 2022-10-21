package ru.yandex.practicum.service.questions;

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
import ru.yandex.practicum.service.dto.questions.NewAnswerDto;
import ru.yandex.practicum.service.dto.questions.NewQuestionDto;
import ru.yandex.practicum.service.dto.questions.QuestionDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.questions.QuestionMapper;
import ru.yandex.practicum.service.models.*;
import ru.yandex.practicum.service.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class QuestionPrivateTests {
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
    private QuestionRepository questionRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private List<QuestionDto> dtos = new ArrayList<>();
    private String body;
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private Category category = new Category(1L, "концерты");
    private Event event1 = Event.builder()
            .id(1L)
            .annotation("Концерт Н.Носкова")
            .category(category)
            .createdOn(LocalDateTime.now().plusDays(5L))
            .description("Концерт известного музыканта Н.Носкова")
            .eventDate(LocalDateTime.now().plusDays(2L))
            .initiator(user1)
            .lat(54.1838F)
            .lon(45.1749F)
            .paid(true)
            .participantLimit(0)
            .publishedOn(LocalDateTime.now().plusDays(4L))
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
        String sqlQuery = "ALTER TABLE questions ALTER COLUMN id RESTART WITH 1";
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
        eventRepository.save(event1);
        eventRepository.save(event2);
    }

    /**
     * Тестирование выдачи вопросов
     * @throws Exception
     */

    @Test
    @Transactional
    public void getQuestions() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/1/events/1/questions"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        Question question = new Question(1L, "Сколько стоит участие?", null, event1, user2, LocalDateTime.now());
        dtos.add(QuestionMapper.toQuestionDto(question));
        questionRepository.save(question);
        this.mockMvc.perform(get("/users/1/events/1/questions?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(get("/users/5/events/1/questions"))
                .andExpect(status().isNotFound());

        //несуществующуу событие
        this.mockMvc.perform(get("/users/1/events/10/questions"))
                .andExpect(status().isNotFound());

        //некорректный from
        this.mockMvc.perform(get("/users/1/events/1/questions?from=-5"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Тестирование добавления вопроса
     * @throws Exception
     */
    @Test
    @Transactional
    public void addQuestions() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/1/events/1/questions"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        NewQuestionDto dto = new NewQuestionDto("Сколько стоит участие?");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/2/events/1/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is(dto.getText())))
                .andExpect(jsonPath("$.eventId", is(1L), Long.class))
                .andExpect(jsonPath("$.authorName", is(user2.getName())));
        assertEquals(1, questionRepository.findAll().size());

        //работа метода с некорректными параметрами
        //событие уже состоялось
        event1.setEventDate(LocalDateTime.now().plusDays(2L));
        eventRepository.save(event1);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/2/events/2/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        event1.setEventDate(LocalDateTime.now().minusDays(1L));
        eventRepository.save(event1);

        //несуществующий пользователь
        this.mockMvc.perform(post("/users/3/events/2/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(post("/users/2/events/5/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //организатор события пытается добавить к нему вопрос
        this.mockMvc.perform(post("/users/1/events/1/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //пустой вопрос
        dto.setText("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/2/events/1/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        dto = null;
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/2/events/1/questions")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Тестирование добавления ответа на вопрос
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateQuestion() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/1/events/1/questions"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        Question question = new Question(1L, "Сколько стоит участие?", null, event1, user2, LocalDateTime.now());
        questionRepository.save(question);
        NewAnswerDto dto = new NewAnswerDto("1000 рублей");
        question.setAnswer(dto.getAnswer());
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/users/1/events/1/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(QuestionMapper.toQuestionDto(question))));
        assertEquals(1, questionRepository.findAll().size());

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/3/events/1/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(patch("/users/1/events/5/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //не организатор события пытается добавить ответ на вопрос
        this.mockMvc.perform(patch("/users/2/events/1/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //пустой вопрос
        dto.setAnswer("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/users/1/events/1/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        dto = null;
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(patch("/users/1/events/1/questions/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /**
     * тестирование удаления вопроса
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteComment() throws Exception{
        //работа метода с корректными параметрами
        Question question = new Question(1L, "Сколько стоит участие?", null, event1, user2, LocalDateTime.now());
        questionRepository.save(question);
        assertEquals(1, questionRepository.findAll().size());
        this.mockMvc.perform(delete("/users/2/events/1/questions/1"))
                .andExpect(status().isOk());
        assertEquals(0, questionRepository.findAll().size());
        question.setId(2L);
        questionRepository.save(question);
        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(delete("/users/8/events/1/questions/2"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(delete("/users/2/events/5/questions/2"))
                .andExpect(status().isNotFound());

        //несуществующий вопрос
        this.mockMvc.perform(delete("/users/2/events/1/questions/1"))
                .andExpect(status().isNotFound());

        //пользователь, не являющийся автором вопроса
        this.mockMvc.perform(delete("/users/1/events/1/questions/2"))
                .andExpect(status().isBadRequest());
    }

}
