package ru.yandex.practicum.service.comments;

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
import ru.yandex.practicum.service.dto.comments.CommentDto;
import ru.yandex.practicum.service.enums.CommentStatus;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.mappers.comments.CommentMapper;
import ru.yandex.practicum.service.models.Category;
import ru.yandex.practicum.service.models.Comment;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.CommentRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
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
public class CommentAdminTests {
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

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователя и обращение в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE comments ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception{
        //работа метода с корректными параметрами
        Category category = new Category(1L, "концерты");
        categoryRepository.save(category);
        User user = new User(1L, "Иван Иванов", "ivanov@mail.ru");
        userRepository.save(user);
        Event event = Event.builder()
                .id(1L)
                .annotation("Концерт Н.Носкова")
                .category(category)
                .createdOn(LocalDateTime.now())
                .description("Концерт известного музыканта Н.Носкова")
                .eventDate(LocalDateTime.now().plusDays(2L))
                .initiator(user)
                .lat(54.1838F)
                .lon(45.1749F)
                .paid(true)
                .participantLimit(100L)
                .publishedOn(LocalDateTime.now().plusHours(3L))
                .requestModeration(false)
                .title("Концерт")
                .state(State.PUBLISHED)
                .commentModeration(true)
                .build();
        eventRepository.save(event);
        Comment comment = new Comment(1L, "отличное мероприятие", event, user, LocalDateTime.now(), CommentStatus.NO_CHANGES);
        commentRepository.save(comment);
        List<CommentDto> dtos = new ArrayList<>();
        dtos.add(CommentMapper.toCommentDto(comment));
        assertEquals(dtos, commentRepository.findAll().stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        dtos.remove(0);
        this.mockMvc.perform(delete("/admin/comments/1"))
                .andExpect(status().isOk());
        assertEquals(dtos.size(), commentRepository.findAll().size());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(delete("/admin/comments/5"))
                .andExpect(status().isNotFound());
    }
}
