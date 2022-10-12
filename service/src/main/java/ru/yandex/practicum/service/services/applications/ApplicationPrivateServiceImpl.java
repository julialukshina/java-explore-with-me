package ru.yandex.practicum.service.services.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.dto.applications.NewApplicationDto;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppReasonConverter;
import ru.yandex.practicum.service.enums.AppStatus;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;
import ru.yandex.practicum.service.mappers.applications.ApplicationMapper;
import ru.yandex.practicum.service.models.Application;
import ru.yandex.practicum.service.repositories.ApplicationRepository;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationPrivateServiceImpl implements ApplicationPrivateService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AppReasonConverter reasonConverter;
    private final static int SIZE = 20;

    @Autowired
    public ApplicationPrivateServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository, AppReasonConverter reasonConverter) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.reasonConverter = reasonConverter;
    }

    /**
     * Создание обращения
     *
     * @param userId Long
     * @param dto    NewApplicationDto
     * @return ApplicationDto
     */
    @Override
    @Transactional
    public ApplicationDto addApplication(Long userId, NewApplicationDto dto) {
        userValidation(userId);
        AppReason reason = reasonConverter.convert(dto.getAppReason());
        if (reason.equals(AppReason.UNSUPPORTED_REASON)) {
            throw new ValidationException(String.format("Обращение с причиной '%s' невозможно", dto.getAppReason()));
        }
        Application application = applicationRepository.save(new Application(0,
                dto.getText(),
                userRepository.findById(userId).get(),
                LocalDateTime.now(),
                AppStatus.PENDING,
                reason));
        log.info("Обращение с id={} создано", application.getId());
        return ApplicationMapper.toApplicationDto(application);
    }

    /**
     * Предоставление пользователю списка его обращений
     *
     * @param userId Long
     * @param from   int
     * @return List<ApplicationDto>
     */
    @Override
    @Transactional
    public List<ApplicationDto> getApplications(Long userId, int from) {
        userValidation(userId);
        Pageable pageable = PageRequest.of(from, SIZE);
        List<ApplicationDto> dtos = applicationRepository.findByAuthorId(userId, pageable).stream()
                .map(ApplicationMapper::toApplicationDto)
                .collect(Collectors.toList());
        log.info("Пользователю с id={} предоставлены его обращения", userId);
        return dtos;
    }

    /**
     * Удаление обращения его автором
     *
     * @param userId Long
     * @param appId  Long
     */
    @Override
    public void cancelApplication(Long userId, Long appId) {
        userValidation(userId);
        appValidation(appId, userId);
        Application application = applicationRepository.findById(appId).get();
        application.setAppStatus(AppStatus.CANCELED);
        applicationRepository.save(application);
        log.info("Обращение с id={} закрыто его автором", appId);
    }

    /**
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    /**
     * Проверка наличия обращения в базе по id и что данный пользователь является его автором
     *
     * @param appId  Long
     * @param userId Long
     */

    private void appValidation(Long appId, Long userId) {
        if (!applicationRepository.existsById(appId)) {
            throw new NotFoundException(String.format("Обращение с id = '%s' не найдено", appId));
        }
        if (applicationRepository.findById(appId).get().getAuthor().getId() != userId) {
            throw new ValidationException(String.format("Пользователь с id = '%s' не является автором обращения с id = '%s'", userId, appId));
        }
    }
}
