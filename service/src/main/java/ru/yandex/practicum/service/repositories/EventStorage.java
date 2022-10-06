package ru.yandex.practicum.service.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.mappers.CategoryMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.services.categories.CategoryPublicService;
import ru.yandex.practicum.service.services.users.UserAdminService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private StateEnumConverter converter = new StateEnumConverter();
    private final UserAdminService userAdminService;
    private final CategoryPublicService categoryPublicService;
    private final RequestRepository requestRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    public EventStorage(JdbcTemplate jdbcTemplate,
                        UserAdminService userAdminService,
                        CategoryPublicService categoryPublicService, RequestRepository requestRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userAdminService = userAdminService;
        this.categoryPublicService = categoryPublicService;
        this.requestRepository = requestRepository;
    }

 public List<Event> getEvents(String sqlQuery){
       return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs));
 }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        rs.getTimestamp("created_on");
        String annotation = rs.getString("annotation");
        Long categoryId = rs.getLong("category_id");
        LocalDateTime createdOn = rs.getTimestamp("created_on").toLocalDateTime();
                //LocalDateTime.parse(rs.getTimestamp("created_on"), formatter);
        String description = rs.getString("description");
        LocalDateTime eventDate = rs.getTimestamp("event_date").toLocalDateTime();
                //LocalDateTime.parse(rs.getString("event_date"), formatter);
        Long userId = rs.getLong("initiator_id");
        Boolean paid = rs.getBoolean("paid");
        Long participantLimit = rs.getLong("participant_limit");
        LocalDateTime publishedOn = null;
        if(rs.getTimestamp("published_on")!=null) {
             publishedOn = rs.getTimestamp("published_on").toLocalDateTime();
        }
                //LocalDateTime.parse(rs.getString("published_on"), formatter);
        Boolean requestModeration = rs.getBoolean("request_moderation");
        State state = converter.convert(rs.getString("state"));
        String title = rs.getString("title");
        Event event = new Event(id,
                annotation,
                CategoryMapper.toCategory(categoryPublicService.getCategoryById(categoryId)),
                createdOn,
                description,
                eventDate,
                userAdminService.getUserById(userId),
                paid,
                participantLimit,
                publishedOn,
                requestModeration,
                title,
                state);

        return event;
    }
}
