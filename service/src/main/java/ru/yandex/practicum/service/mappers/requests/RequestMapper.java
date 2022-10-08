package ru.yandex.practicum.service.mappers.requests;

import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.models.Request;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                request.getCreated().toString(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }
}
