package ru.yandex.practicum.service.mappers.users;

import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.models.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User toUser(UserDto dto) {
        return new User(dto.getId(), dto.getEmail(), dto.getName());
    }
}
