package ru.yandex.practicum.service.applications;

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
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ApplicationAdminTests {
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
    private User user = new User(1L, "Jack", "jack@yandex.ru");
    private Application application1 = Application.builder()
            .id(1L)
            .text("некорректное поведение пользователя")
            .author(user)
            .created(LocalDateTime.now())
            .appStatus(AppStatus.PENDING)
            .appReason(AppReason.DELETE_USER)
            .build();
    private Application application2 = Application.builder()
            .id(2L)
            .text("некорректный комментарий")
            .author(user)
            .created(LocalDateTime.now())
            .appStatus(AppStatus.APPROVED)
            .appReason(AppReason.DELETE_COMMENT)
            .build();


    @BeforeEach //перед каждым тестом скидываем счетчики, добавляем пользователя и обращение в репозитории
    public void createObject() {
        String sqlQuery = "ALTER TABLE applications ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE events ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        userRepository.save(user);
        applicationRepository.save(application1);
        applicationRepository.save(application2);
    }

    /**
     * Тестирование выдачи списка всех обращений
     * @throws Exception
     */
    @Test
    @Transactional
    public void getApplications() throws Exception{
        dtos.addAll(Arrays.asList(ApplicationMapper.toApplicationDto(application1), ApplicationMapper.toApplicationDto(application2)));
        //корректная работа метода без параметров
        this.mockMvc.perform(get("/admin/applications"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/admin/applications?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/admin/applications?from=-1"))
                .andExpect(status().isInternalServerError());

    }

    /**
     * Тестирование выдачи списка обращений по причине
     * @throws Exception
     */
    @Test
    @Transactional
    public void getApplicationsByReason() throws Exception{
        dtos.add(ApplicationMapper.toApplicationDto(application1));
        //корректная работа метода без параметров
        this.mockMvc.perform(get("/admin/applications/reason/DELETE_USER"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/admin/applications/reason/DELETE_USER?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/admin/applications/reason/DELETE_USER?from=-1"))
                .andExpect(status().isInternalServerError());
        this.mockMvc.perform(get("/admin/applications/reason/update_USER?from=1"))
                .andExpect(status().isBadRequest());
    }


    /**
     * Тестирование выдачи списка обращений по статусу
     * @throws Exception
     */
    @Test
    @Transactional
    public void getApplicationsByStatus() throws Exception{
        dtos.add(ApplicationMapper.toApplicationDto(application2));
        //корректная работа метода без параметров
        this.mockMvc.perform(get("/admin/applications/status/APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с корректными параметрами
        this.mockMvc.perform(get("/admin/applications/status/APPROVED?from=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        //работа метода с некорректными параметрами
        this.mockMvc.perform(get("/admin/applications/status/APPROVED?from=-1"))
                .andExpect(status().isInternalServerError());
        this.mockMvc.perform(get("/admin/applications/status/update?from=1"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тестирование отклонения обращения
     * @throws Exception
     */
    @Test
    @Transactional
    public void rejectApplication() throws Exception{
        //корректная работа метода
        this.mockMvc.perform(patch("/admin/applications/1/reject"))
                .andExpect(status().isOk());
        assertEquals(AppStatus.REJECTED, applicationRepository.findById(1L).get().getAppStatus());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(patch("/admin/applications/10/reject"))
                .andExpect(status().isNotFound());
    }

    /**
     * Тестирование одобрения обращения
     * @throws Exception
     */
    @Test
    @Transactional
    public void approvedApplication() throws Exception{
        //корректная работа метода
        this.mockMvc.perform(patch("/admin/applications/1/approve"))
                .andExpect(status().isOk());
        assertEquals(AppStatus.APPROVED, applicationRepository.findById(1L).get().getAppStatus());

        //работа метода с некорректными параметрами
        this.mockMvc.perform(patch("/admin/applications/10/approve"))
                .andExpect(status().isNotFound());
    }
}
