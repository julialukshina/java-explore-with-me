package ru.yandex.practicum.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.StatusEnumConverter;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.services.events.EventPublicService;
import ru.yandex.practicum.service.services.users.UserAdminService;

import java.time.LocalDateTime;

@Lazy
@Component
public class RequestMapper {
    private static StatusEnumConverter converter = new StatusEnumConverter();
    private final UserAdminService userAdminService;
    private final EventPublicService eventPublicService;

    @Autowired
    public RequestMapper(UserAdminService userAdminService, EventPublicService eventPublicService) {
        this.userAdminService = userAdminService;
        this.eventPublicService = eventPublicService;
    }

    // TODO: 24.09.2022 подгурзить ивент, user
    public ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                request.getCreated().toString(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }

    public Request toRequest(ParticipationRequestDto dto) {
        return new Request(dto.getId(),
                LocalDateTime.parse(dto.getCreated()),
                eventPublicService.getById(dto.getEvent()),
                userAdminService.getUserById(dto.getRequester()),
                converter.convert(dto.getStatus()));
    }
}
