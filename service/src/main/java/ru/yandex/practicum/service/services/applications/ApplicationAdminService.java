package ru.yandex.practicum.service.services.applications;

import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppStatus;

import java.util.List;

public interface ApplicationAdminService {
    List<ApplicationDto> getApplicationsByReason(AppReason appReason, int from);

    List<ApplicationDto> getApplicationsByStatus(AppStatus appStatus, int from);

    void rejectApplication(Long appId);

    void approveApplication(Long appId);

    List<ApplicationDto> getApplications(int from);
}
