package ru.yandex.practicum.service.controllers.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.requests.ParticipationRequestDto;
import ru.yandex.practicum.service.services.requests.RequestPrivateService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class RequestPrivateController {
    private final RequestPrivateService service;

    public RequestPrivateController(RequestPrivateService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable Long userId) {
        return service.getRequestsOfUser(userId);
    }

    @PostMapping
    public ParticipationRequestDto postRequest(@PathVariable Long userId, @RequestParam(name = "eventId") Long eventId) {
        return service.postRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}
