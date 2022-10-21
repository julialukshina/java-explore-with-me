package ru.yandex.practicum.service.comments;

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
import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.dto.comments.InputCommentDto;
import ru.yandex.practicum.service.enums.CommentStatus;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.comments.CommentMapper;
import ru.yandex.practicum.service.models.*;
import ru.yandex.practicum.service.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class CommentPrivateTests {
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
    private CommentRepository commentRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RequestRepository requestRepository;
    private List<CommentDto> dtos = new ArrayList<>();
    private String body;
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private User user3 = new User(3L, "Иван Смирнов", "smirnov@mail.ru");
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
        String sqlQuery = "ALTER TABLE comments ALTER COLUMN id RESTART WITH 1";
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
     * тестирование создания комментария
     * @throws Exception
     */
    @Test
    @Transactional
    public void addComment() throws Exception{
        //работа метода с корректными параметрами
        //добавление комментария к событию без лимита заявок
        this.mockMvc.perform(get("/users/1/events/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        InputCommentDto dto = new InputCommentDto("отличное мероприятие");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/events/1/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is(dto.getText())))
                .andExpect(jsonPath("$.commentStatus", is(CommentStatus.NO_CHANGES.toString())));
        assertEquals(1, commentRepository.findAll().size());
        //организатор события добавляет комментарий к событию с лимитом заявок
        this.mockMvc.perform(post("/users/1/events/2/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.text", is(dto.getText())))
                .andExpect(jsonPath("$.commentStatus", is(CommentStatus.NO_CHANGES.toString())));

        //добавление комментария к событию с лимитом заявок
        Request request = new Request(1L, LocalDateTime.now(), event2, user2, Status.CONFIRMED);
        requestRepository.save(request);
        this.mockMvc.perform(post("/users/2/events/2/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.text", is(dto.getText())));
        assertEquals(3, commentRepository.findAll().size());

        //работа метода с некорректными параметрами
        //событие еще не состоялось
        event1.setEventDate(LocalDateTime.now().plusDays(2L));
        eventRepository.save(event1);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/events/1/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        event1.setEventDate(LocalDateTime.now().minusDays(1L));
        eventRepository.save(event1);

        //несуществующий пользователь
        this.mockMvc.perform(post("/users/3/events/2/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(post("/users/2/events/5/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //событие с лимитом участников, заявка пользователя не подтверждена
        userRepository.save(user3);
        this.mockMvc.perform(post("/users/3/events/2/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //организатор запретил добавлять комментарии
        event2.setCommentModeration(false);
        eventRepository.save(event2);
        this.mockMvc.perform(post("/users/2/events/2/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //пустой комментарий
        dto.setText("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/events/1/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        dto = null;
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/events/1/comments")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    /**
     * тестирование выдачи пользователю комментариев
     * @throws Exception
     */
    @Test
    @Transactional
    public void getComments() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/1/events/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        Comment comment = new Comment(1L, "good event", event1, user2, LocalDateTime.now(), CommentStatus.NO_CHANGES);
        dtos.add(CommentMapper.toCommentDto(comment));
        commentRepository.save(comment);
        this.mockMvc.perform(get("/users/1/events/1/comments?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(get("/users/5/events/1/comments"))
                .andExpect(status().isNotFound());

        //несуществующуу событие
        this.mockMvc.perform(get("/users/1/events/10/comments"))
                .andExpect(status().isNotFound());

        //некорректный from
        this.mockMvc.perform(get("/users/1/events/1/comments?from=-5"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * тестирование редактирования комментария
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateComment() throws Exception{
        //работа метода с корректными параметрами
        Comment comment = new Comment(1L, "good event", event1, user2, LocalDateTime.now(), CommentStatus.NO_CHANGES);
        commentRepository.save(comment);
        assertEquals(1, commentRepository.findAll().size());
        comment.setText("updated comment");
        InputCommentDto inputCommentDto = new InputCommentDto("updated comment");
        body = objectMapper.writeValueAsString(inputCommentDto);
        this.mockMvc.perform(patch("/users/2/events/1/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.commentStatus", is(CommentStatus.EDITED.toString())));
        assertEquals(1, commentRepository.findAll().size());

        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(patch("/users/5/events/1/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующуу событие
        this.mockMvc.perform(patch("/users/2/events/6/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //несуществующий комментарий
        this.mockMvc.perform(patch("/users/2/events/1/comments/10")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //пользователь, не являющийся автором комментария
        this.mockMvc.perform(patch("/users/1/events/1/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //комментарий не относится к событию
        comment.setId(2);
        commentRepository.save(comment);
        System.out.println(commentRepository.findById(2L));
        this.mockMvc.perform(patch("/users/2/events/2/comments/2")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //некорректное body
        inputCommentDto.setText("");
        body = objectMapper.writeValueAsString(inputCommentDto);
        this.mockMvc.perform(patch("/users/2/events/1/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        inputCommentDto.setText(null);
        body = objectMapper.writeValueAsString(inputCommentDto);
        this.mockMvc.perform(patch("/users/2/events/1/comments/1")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование удаления комментария
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteComment() throws Exception{
        //работа метода с корректными параметрами
        Comment comment = new Comment(1L, "good event", event1, user2, LocalDateTime.now(), CommentStatus.NO_CHANGES);
        commentRepository.save(comment);
        assertEquals(1, commentRepository.findAll().size());
        this.mockMvc.perform(delete("/users/2/events/1/comments/1"))
                .andExpect(status().isOk());
        assertEquals(0, commentRepository.findAll().size());
        comment.setId(2L);
        commentRepository.save(comment);
        //работа метода с некорректными параметрами
        //несуществующий пользователь
        this.mockMvc.perform(delete("/users/8/events/1/comments/2"))
                .andExpect(status().isNotFound());

        //несуществующее событие
        this.mockMvc.perform(delete("/users/2/events/5/comments/2"))
                .andExpect(status().isNotFound());

        //несуществующий комментарий
        this.mockMvc.perform(delete("/users/2/events/1/comments/1"))
                .andExpect(status().isNotFound());

        //пользователь, не являющийся автором комментария
        this.mockMvc.perform(delete("/users/1/events/1/comments/2"))
                .andExpect(status().isBadRequest());
    }
}
