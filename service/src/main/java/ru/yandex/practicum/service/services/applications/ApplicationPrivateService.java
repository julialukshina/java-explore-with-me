package ru.yandex.practicum.service.services.applications;

import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.dto.applications.NewApplicationDto;

import java.util.List;

public interface ApplicationPrivateService {
    ApplicationDto addApplication(Long userId, NewApplicationDto dto);

    List<ApplicationDto> getApplications(Long userId, int from);

    void cancelApplication(Long userId, Long appId);
}
