package ru.yandex.practicum.service.controllers.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.dto.applications.ApplicationDto;
import ru.yandex.practicum.service.dto.applications.NewApplicationDto;
import ru.yandex.practicum.service.services.applications.ApplicationPrivateService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/applications")
@Slf4j
@Validated
public class ApplicationPrivateController {
    private final ApplicationPrivateService service;

    @Autowired
    public ApplicationPrivateController(ApplicationPrivateService service) {
        this.service = service;
    }

    @PostMapping
    public ApplicationDto addApplication(@PathVariable Long userId, @RequestBody NewApplicationDto dto) {
        return service.addApplication(userId, dto);
    }

    @GetMapping
    public List<ApplicationDto> getComments(@PathVariable Long userId,
                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from) {
        return service.getApplications(userId, from);
    }

    @PatchMapping("/{appId}")
    public void cancelApplication(@PathVariable Long userId,
                                  @PathVariable Long appId) {
        service.cancelApplication(userId, appId);
    }
}
