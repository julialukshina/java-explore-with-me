package ru.yandex.practicum.service.mappers.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.enums.StatusEnumConverter;
import ru.yandex.practicum.service.models.Request;
import ru.yandex.practicum.service.services.events.EventPublicService;
import ru.yandex.practicum.service.services.users.UserAdminService;

import java.time.LocalDateTime;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                request.getCreated().toString(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }
}
