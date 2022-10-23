package ru.yandex.practicum.statistics.controlers;

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
import ru.yandex.practicum.statistics.dto.EndpointHit;
import ru.yandex.practicum.statistics.model.Hit;
import ru.yandex.practicum.statistics.repositories.HitRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class StatisticsTests {
    private final EndpointHit dto1 = EndpointHit.builder()
            .app("service")
            .ip("172.23.58.14")
            .uri("events/1")
            .timestamp("2022-10-02 12:00:54")
            .build();

    private final EndpointHit dto2 = EndpointHit.builder()
            .app("service")
            .ip("172.23.58.15")
            .uri("events/1")
            .timestamp("2022-10-02 12:00:54")
            .build();
    private final EndpointHit dto3 = EndpointHit.builder()
            .app("service")
            .ip("172.23.58.14")
            .uri("events/2")
            .timestamp("2022-10-02 12:00:54")
            .build();
    private final EndpointHit dto4 = EndpointHit.builder()
            .app("service")
            .ip("172.23.58.15")
            .uri("events/2")
            .timestamp("2022-10-02 12:00:54")
            .build();
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private HitRepository repository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearEnvironment() {
        String query = "ALTER TABLE hits ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(query);
        query = "TRUNCATE TABLE hits ";
        jdbcTemplate.update(query);
    }

    /**
     * создание записи о просмотре
     */
    @Test
    @Transactional
    public void addHit() throws Exception {
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<Hit> list = repository.findAll();
        assertEquals(1, list.get(0).getId());
        assertEquals(dto1.getApp(), list.get(0).getApp());
        assertEquals(dto1.getIp(), list.get(0).getIp());
    }

    /**
     * получение статистики
     */
    @Test
    @Transactional
    void getStatistics() throws Exception {
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto1.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto1.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto2.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto2.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto3.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto3.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto3))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto4.setTimestamp("2022-10-01 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        dto4.setTimestamp("2022-09-30 12:00:54");
        this.mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto4))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String start = "2022-09-29 12:00:00";
        String end = "2022-10-30 12:00:00";
        String start1 = "2022-09-30 13:00:00";
        try {
            start = URLEncoder.encode(start, StandardCharsets.UTF_8.toString());
            end = URLEncoder.encode(end, StandardCharsets.UTF_8.toString());
            start1 = URLEncoder.encode(start1, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
        String uri1 = "events/1";
        String uri2 = "events/2";


        String path = "/stats?start=" + start + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=false";
        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(6L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(6L), Long.class));

        path = "/stats?start=" + start1 + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=false";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(4L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(4L), Long.class));

        path = "/stats?start=" + start + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=true";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(3L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(3L), Long.class));

        path = "/stats?start=" + start1 + "&end=" + end + "&uris=" + uri1 + "&uris=" + uri2 + "&unique=true";

        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri", is(uri1), String.class))
                .andExpect(jsonPath("$[0].hits", is(2L), Long.class))
                .andExpect(jsonPath("$[1].uri", is(uri2), String.class))
                .andExpect(jsonPath("$[1].hits", is(2L), Long.class));

    }


}