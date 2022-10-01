package ru.yandex.practicum.service.services.users;

import ru.yandex.practicum.service.dto.users.NewUserRequest;
import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.models.User;

import java.util.List;

public interface UserAdminService {

    User getUserById(Long id);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

}
