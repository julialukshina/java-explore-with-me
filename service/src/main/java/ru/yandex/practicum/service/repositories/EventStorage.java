package ru.yandex.practicum.service.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.StateEnumConverter;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.services.categories.CategoryPublicService;
import ru.yandex.practicum.service.services.users.UserAdminService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private StateEnumConverter converter = new StateEnumConverter();
    private final UserAdminService userAdminService;
    private final CategoryPublicService categoryPublicService;

    @Autowired
    public EventStorage(JdbcTemplate jdbcTemplate,
                        UserAdminService userAdminService,
                        CategoryPublicService categoryPublicService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userAdminService = userAdminService;
        this.categoryPublicService = categoryPublicService;
    }

    public List<Event> getEvents(String sqlQuery) {
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs));
    }

    private Event makeEvent(ResultSet rs) throws SQLException {

        Long id = rs.getLong("id");
        rs.getTimestamp("created_on");
        String annotation = rs.getString("annotation");
        Long categoryId = rs.getLong("category_id");
        LocalDateTime createdOn = rs.getTimestamp("created_on").toLocalDateTime();
        String description = rs.getString("description");
        LocalDateTime eventDate = rs.getTimestamp("event_date").toLocalDateTime();
        Long userId = rs.getLong("initiator_id");
        Float lat = rs.getFloat("lat");
        Float lon = rs.getFloat("lon");
        Boolean paid = rs.getBoolean("paid");
        Long participantLimit = rs.getLong("participant_limit");
        LocalDateTime publishedOn = null;
        if (rs.getTimestamp("published_on") != null) {
            publishedOn = rs.getTimestamp("published_on").toLocalDateTime();
        }
        Boolean requestModeration = rs.getBoolean("request_moderation");
        State state = converter.convert(rs.getString("state"));
        String title = rs.getString("title");
        Boolean commentModeration = rs.getBoolean("comment_moderation");
        return new Event(id,
                annotation,
                CategoryMapper.toCategory(categoryPublicService.getCategoryById(categoryId)),
                createdOn,
                description,
                eventDate,
                userAdminService.getUserById(userId),
                lat,
                lon,
                paid,
                participantLimit,
                publishedOn,
                requestModeration,
                title,
                state,
                commentModeration);
    }
}
