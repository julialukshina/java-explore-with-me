package ru.yandex.practicum.service.applications;

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
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.dto.applications.NewApplicationDto;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppStatus;
import ru.yandex.practicum.service.mappers.applications.ApplicationMapper;
import ru.yandex.practicum.service.models.Application;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.ApplicationRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ApplicationPrivateTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    private String body;
    private List<ApplicationDto> dtos = new ArrayList<>();
    private User user1 = new User(1L, "Jack", "jack@yandex.ru");
    private User user2 = new User(2L, "Jam", "jam@yandex.ru");
    private Application application1 = Application.builder()
            .id(1L)
            .text("некорректное поведение пользователя")
            .author(user1)
            .created(LocalDateTime.now())
            .appStatus(AppStatus.PENDING)
            .appReason(AppReason.DELETE_USER)
            .build();
    private Application application2 = Application.builder()
            .id(2L)
            .text("некорректный комментарий")
            .author(user1)
            .created(LocalDateTime.now())
            .appStatus(AppStatus.APPROVED)
            .appReason(AppReason.DELETE_COMMENT)
            .build();


    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователя и обращение в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE applications ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE categories ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        userRepository.save(user1);
        userRepository.save(user2);
        applicationRepository.save(application1);
        applicationRepository.save(application2);
    }

    /**
     * тестирование добавления обращения
     * @throws Exception
     */
    @Test
    @Transactional
    public void addApplication() throws Exception{
        NewApplicationDto dto = new NewApplicationDto("некорректное поведение пользователя", AppReason.DELETE_USER.toString());
        body = objectMapper.writeValueAsString(dto);
        application1.setId(3);
        //работа метода с корректными параметрами
        this.mockMvc.perform(post("/users/1/applications")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(application1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(application1.getText())))
                .andExpect(jsonPath("$.appStatus", is(application1.getAppStatus().toString())))
                .andExpect(jsonPath("$.appReason", is(application1.getAppReason().toString())));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(post("/users/6/applications")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        dto.setText("");
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/applications")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        dto.setText("test");
        dto.setAppReason(null);
        body = objectMapper.writeValueAsString(dto);
        this.mockMvc.perform(post("/users/1/applications")
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * тестирование получение пользователем его обращений
     * @throws Exception
     */
    @Test
    @Transactional
    public void getApplications() throws Exception{
        //корректная работа метода без параметров
        //пользователь без обращений
        this.mockMvc.perform(get("/users/2/applications"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
        dtos.addAll(Arrays.asList(ApplicationMapper.toApplicationDto(application1),ApplicationMapper.toApplicationDto(application2)));
        //пользователь с обращениями
        this.mockMvc.perform(get("/users/1/applications"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/users/1/applications?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        //некорректный from
        this.mockMvc.perform(get("/users/1/applications?from=-1"))
                .andExpect(status().isInternalServerError());
        //некорректный user
        this.mockMvc.perform(get("/users/5/applications?from=0"))
                .andExpect(status().isNotFound());
    }

    /**
     * тестирование закрытие пользователем его обращений
     * @throws Exception
     */
    @Test
    @Transactional
    public void cancelApplication() throws Exception{
        //корректная работа метода
        this.mockMvc.perform(patch("/users/1/applications/1"))
                .andExpect(status().isOk());
        assertEquals(AppStatus.CANCELED, applicationRepository.findById(1L).get().getAppStatus());

        //работа метода с некорректными параметрами
        //некорректный user
        this.mockMvc.perform(patch("/users/2/applications/1"))
                .andExpect(status().isBadRequest());

        //некорректный id обращения
        this.mockMvc.perform(patch("/users/1/applications/8"))
                .andExpect(status().isNotFound());
    }
}
