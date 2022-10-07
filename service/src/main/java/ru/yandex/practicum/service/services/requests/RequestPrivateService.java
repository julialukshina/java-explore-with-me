package ru.yandex.practicum.service.services.requests;

import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {
    List<ParticipationRequestDto> getRequestsOfUser(Long userId);

    ParticipationRequestDto postRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
