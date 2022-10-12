package ru.yandex.practicum.service.services.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppStatus;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.applications.ApplicationMapper;
import ru.yandex.practicum.service.models.Application;
import ru.yandex.practicum.service.repositories.ApplicationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationAdminServiceImpl implements ApplicationAdminService {

    private final ApplicationRepository applicationRepository;
    private final static int SIZE = 20;

    @Autowired
    public ApplicationAdminServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Администратору предоставлен список обращений, отсортированных по их причине и по дате их создания
     *
     * @param appReason AppReason
     * @param from      int
     * @return List<ApplicationDto>
     */
    @Override
    public List<ApplicationDto> getApplicationsByReason(AppReason appReason, int from) {
        Pageable pageable = PageRequest.of(from, SIZE);
        List<ApplicationDto> dtos = applicationRepository.findAllByAppReasonOrderByCreatedAsc(appReason, pageable).stream()
                .map(ApplicationMapper::toApplicationDto)
                .collect(Collectors.toList());
        log.info("Администратору предоставлен список обращений, причина которых {}", appReason);
        return dtos;
    }

    /**
     * Администратору предоставлен список обращений, отсортированных по их статусу и по дате их создания
     *
     * @param appStatus AppStatu
     * @param from      int
     * @return List<ApplicationDto>
     */
    @Override
    public List<ApplicationDto> getApplicationsByStatus(AppStatus appStatus, int from) {
        Pageable pageable = PageRequest.of(from, SIZE);
        List<ApplicationDto> dtos = applicationRepository.findAllByAppStatusOrderByCreatedAsc(appStatus, pageable).stream()
                .map(ApplicationMapper::toApplicationDto)
                .collect(Collectors.toList());
        log.info("Администратору предоставлен список обращений, причина которых {}", appStatus);
        return dtos;
    }

    /**
     * Отклонение обращения
     *
     * @param appId Long
     */
    @Override
    public void rejectApplication(Long appId) {
        appValidation(appId);
        Application application = applicationRepository.findById(appId).get();
        statusValidation(application.getAppStatus());
        application.setAppStatus(AppStatus.REJECTED);
        applicationRepository.save(application);
        log.info("Обращение с id={} отклонено", appId);
    }

    /**
     * Одобрение обращения
     *
     * @param appId Long
     */
    @Override
    public void approveApplication(Long appId) {
        appValidation(appId);
        Application application = applicationRepository.findById(appId).get();
        statusValidation(application.getAppStatus());
        application.setAppStatus(AppStatus.APPROVED);
        applicationRepository.save(application);
        log.info("Обращение с id={} одобрено", appId);
    }

    /**
     * Проверка наличия обращения в базе по id
     *
     * @param id Long
     */
    private void appValidation(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Обращение с id = '%s' не найдено", id));
        }
    }

    private void statusValidation(AppStatus status) {
        if (!status.equals(AppStatus.PENDING)) {
            throw new ValidationException("Только обращения со статусом PENDING могут быть одобрены или отклонены");
        }
    }
}
