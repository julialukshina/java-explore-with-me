package ru.yandex.practicum.service.mappers.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.dto.users.UserShortDto;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.UserRepository;

@Lazy
@Component
public class UserShortDtoMapper {
    private final UserRepository repository;

    @Autowired
    public UserShortDtoMapper(UserRepository repository) {
        this.repository = repository;
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public User toUser(UserShortDto dto) {
        return new User(dto.getId(), dto.getName(), repository.findById(dto.getId()).get().getEmail());

    }
}
