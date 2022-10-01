package ru.yandex.practicum.service.controllers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.dto.users.NewUserRequest;
import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.services.users.UserAdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
public class UserAdminController {
    private final UserAdminService service;

    @Autowired
    public UserAdminController(UserAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return service.getUsers(ids, from, size);
    }

    @PostMapping
    public UserDto createUser(@RequestBody NewUserRequest newUserRequest) {
        return service.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
    }


}
