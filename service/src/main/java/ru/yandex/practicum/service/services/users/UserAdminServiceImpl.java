package ru.yandex.practicum.service.services.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.MyPageable;
import ru.yandex.practicum.service.dto.users.NewUserRequest;
import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.mappers.users.UserMapper;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;


    @Autowired
    public UserAdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = MyPageable.of(from, size);
        if (!ids.isEmpty()) {
            for (Long id :
                    ids) {
                userValidation(id);
            }
            return userRepository.findAllByIds(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = new User(0, newUserRequest.getName(), newUserRequest.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userValidation(userId);
        userRepository.deleteById(userId);
    }

    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new MyNotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    @Override
    public User getUserById(Long id) {
        userValidation(id);
        return userRepository.findById(id).get();
    }
}
