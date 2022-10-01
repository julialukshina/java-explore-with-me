package ru.yandex.practicum.service.mappers.users;

import ru.yandex.practicum.service.dto.users.UserShortDto;
import ru.yandex.practicum.service.models.User;

public class UserShortDtoMapper {

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    // TODO: 24.09.2022 придумать что-то с емэйлом
    public static User toUser(UserShortDto dto) {
        return new User(dto.getId(), dto.getName(), null);

    }
}
