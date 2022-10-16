package ru.yandex.practicum.service.mappers.applications;

import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.models.Application;

import java.time.format.DateTimeFormatter;

public class ApplicationMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ApplicationDto toApplicationDto(Application application) {
        return new ApplicationDto(application.getId(),
                application.getText(),
                application.getAuthor().getId(),
                application.getCreated().format(formatter),
                application.getAppStatus().toString(),
                application.getAppReason().toString());
    }
}
