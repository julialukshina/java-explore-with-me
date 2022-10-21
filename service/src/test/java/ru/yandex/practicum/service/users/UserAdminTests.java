package ru.yandex.practicum.service.users;

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
import ru.yandex.practicum.service.dto.users.NewUserRequest;
import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.mappers.users.UserMapper;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class UserAdminTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    String body;
    private User user1 = new User(1L, "Иван Иванов", "ivanov@mail.ru");
    private User user2 = new User(2L, "Иван Петров", "petrov@mail.ru");
    private User user3 = new User(3L, "Иван Смирнов", "smirnov@mail.ru");
    private List<UserDto> dtos = new ArrayList<>();

    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователей, категорию и события в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
    }

    /**
     * Тестирование создания пользователя
     * @throws Exception
     */
    @Test
    @Transactional
    public void addUser() throws Exception{
        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        NewUserRequest newUserRequest = new NewUserRequest("ivanov@yandex.ru", "ivan");
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(newUserRequest.getName())))
                .andExpect(jsonPath("$.email", is(newUserRequest.getEmail())));

        //работа метода с некорректными параметрами
        //некорректный email
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        newUserRequest.setEmail(null);
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        newUserRequest.setEmail("");
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        newUserRequest.setEmail("ivanov");
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //некорректное имя
        newUserRequest.setEmail("ivanov@mail.ru");
        newUserRequest.setName(null);
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        newUserRequest.setName("");
        body = objectMapper.writeValueAsString(newUserRequest);
        this.mockMvc.perform(post("/admin/users")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тестирование выдачи списка пользователей
     * @throws Exception
     */
    @Test
    @Transactional
    public void getUsers() throws Exception{
        //работа метода без параметров
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        dtos.addAll(Arrays.asList(UserMapper.toUserDto(user1), UserMapper.toUserDto(user2), UserMapper.toUserDto(user3)));
        this.mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        //работа метода с корректными параметрами
        //со всеми параметрами
        this.mockMvc.perform(get("/admin/users?ids=1,2,3&from=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        //только ids - заполненный и пустой
        dtos.remove(0);
        this.mockMvc.perform(get("/admin/users?ids=2,3"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        this.mockMvc.perform(get("/admin/users?ids="))
                .andExpect(status().isOk());

        //только from
        this.mockMvc.perform(get("/admin/users?from=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //только size
        dtos.clear();
        dtos.add(UserMapper.toUserDto(user1));
        this.mockMvc.perform(get("/admin/users?size=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        // в ids несуществующий пользователь
        this.mockMvc.perform(get("/admin/users?ids=1,2,5&from=0&size=10"))
                .andExpect(status().isNotFound());

        //некорректный from
        this.mockMvc.perform(get("/admin/users?ids=1&from=-1&size=10"))
                .andExpect(status().isInternalServerError());

        //некорректный size
        this.mockMvc.perform(get("/admin/users?ids=1&from=0&size=0"))
                .andExpect(status().isInternalServerError());
        }

    /**
     * Тестирование удаления пользователя
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteUser() throws Exception{
        //работа метода с корректными параметрами
        userRepository.save(user1);
        dtos.add(UserMapper.toUserDto(user1));
        assertEquals(dtos.size(), userRepository.findAll().size());
        this.mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk());
        dtos.remove(0);
        assertEquals(dtos.size(), userRepository.findAll().size());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(delete("/admin/users/5"))
                .andExpect(status().isNotFound());
    }
}
