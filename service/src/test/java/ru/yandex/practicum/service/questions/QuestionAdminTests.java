package ru.yandex.practicum.service.questions;

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
import ru.yandex.practicum.service.dto.questions.QuestionDto;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.comments.CommentMapper;
import ru.yandex.practicum.service.mappers.questions.QuestionMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.Question;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.QuestionRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class QuestionAdminTests {
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



    @BeforeEach //перед каждым тестом скидываем счетчики
    public void createObject() {
        String sqlQuery = "ALTER TABLE questions ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    /**
     * Тестирование удаления вопроса администратором
     * @throws Exception
     */

    @Test
    @Transactional
    public void deleteQuestion() throws Exception{
        List<QuestionDto> dtos = new ArrayList<>();
        User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
        User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
        Category category = new Category(1L, "концерты");
        Event event = Event.builder()
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
        categoryRepository.save(category);
        userRepository.save(user1);
        userRepository.save(user2);
        eventRepository.save(event);

        Question question = new Question(1L, "Сколько стоит участие?", null, event, user2, LocalDateTime.now());
        dtos.add(QuestionMapper.toQuestionDto(question));
        questionRepository.save(question);
        assertEquals(dtos, questionRepository.findAll().stream().map(QuestionMapper::toQuestionDto).collect(Collectors.toList()));
        dtos.remove(0);
        this.mockMvc.perform(delete("/admin/questions/1"))
                .andExpect(status().isOk());
        assertEquals(dtos.size(), questionRepository.findAll().size());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(delete("/admin/questions/5"))
                .andExpect(status().isNotFound());

    }
}
