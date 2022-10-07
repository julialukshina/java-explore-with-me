package ru.yandex.practicum.service.mappers.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.mappers.categories.CategoryMapper;
import ru.yandex.practicum.service.mappers.users.UserShortDtoMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.RequestRepository;

import java.time.format.DateTimeFormatter;

@Lazy
@Component
public class EventShortMapper {

    private final RequestRepository requestRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Lazy
    private final UserShortDtoMapper userShortDtoMapper;

    @Autowired
    public EventShortMapper(RequestRepository requestRepository, UserShortDtoMapper userShortDtoMapper) {
        this.requestRepository = requestRepository;
        this.userShortDtoMapper = userShortDtoMapper;
    }

    public EventShortDto toEventShortDto(Event event) {
        long eventConfirmedRequest = 0;
        if (requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED) != null) {
            eventConfirmedRequest = requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        }
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                eventConfirmedRequest,
                event.getEventDate().format(formatter),
                userShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                0);
    }
}
