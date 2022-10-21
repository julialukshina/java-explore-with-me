package ru.yandex.practicum.service.controllers.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppReasonConverter;
import ru.yandex.practicum.service.enums.AppStatus;
import ru.yandex.practicum.service.enums.AppStatusConverter;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.services.applications.ApplicationAdminService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/applications")
@Slf4j
@Validated
public class ApplicationAdminController {

    private final ApplicationAdminService service;
    private final AppReasonConverter reasonConverter;

    private final AppStatusConverter statusConverter;

    @Autowired
    public ApplicationAdminController(ApplicationAdminService service,
                                      AppReasonConverter reasonConverter,
                                      AppStatusConverter statusConverter) {
        this.service = service;
        this.reasonConverter = reasonConverter;
        this.statusConverter = statusConverter;
    }

    @GetMapping
    public List<ApplicationDto> getApplications(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        return service.getApplications(from);
    }
    @GetMapping("/reason/{reason}")
    public List<ApplicationDto> getApplicationsByReason(@PathVariable String reason,
                                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        AppReason appReason = reasonConverter.convert(reason);
        if(appReason==null){
            throw new ValidationException("В данный метод для поиска должна быть передана причина обращения");
        }
        if (AppReason.UNSUPPORTED_REASON.equals(appReason)) {
            throw new ValidationException(String.format("Обращений с причиной '%s' не существует", reason));
        }
        return service.getApplicationsByReason(appReason, from);
    }

    @GetMapping("/status/{status}")
    public List<ApplicationDto> getApplicationsByStatus(@PathVariable String status,
                                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        System.out.println(status);
        AppStatus appStatus = statusConverter.convert(status);
        System.out.println(appStatus);
        if(appStatus==null){
            throw new ValidationException("В данный метод для поиска должен быть передан статус обращения");
        }
        if (AppStatus.UNSUPPORTED_STATUS.equals(appStatus)) {
            throw new ValidationException(String.format("Обращений со статусом '%s' не существует", status));
        }
        return service.getApplicationsByStatus(appStatus, from);
    }

    @PatchMapping("/{appId}/reject")
    public void rejectApplication(@PathVariable Long appId) {
        service.rejectApplication(appId);
    }

    @PatchMapping("/{appId}/approve")
    public void approveApplication(@PathVariable Long appId) {
        service.approveApplication(appId);
    }
}
