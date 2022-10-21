package ru.yandex.practicum.service.services.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.Pageable;
import ru.yandex.practicum.service.dto.users.NewUserRequest;
import ru.yandex.practicum.service.dto.users.UserDto;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.mappers.users.UserMapper;
import ru.yandex.practicum.service.models.User;
import ru.yandex.practicum.service.repositories.UserRepository;

import java.util.ArrayList;
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

    /**
     * Выдача администратору списка пользователей по заданным id
     *
     * @param ids  List<Long>
     * @param from int
     * @param size int
     * @return List<UserDto>
     */
    @Override
    @Transactional
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        org.springframework.data.domain.Pageable pageable = Pageable.of(from, size);
        List<UserDto> dtos;

        if(ids==null || ids.isEmpty()){
          dtos = userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
            log.info("Администратору выдан список пользователей");
            return dtos;
        }else {
            for (Long id :
                    ids) {
                userValidation(id);
            }
            dtos = userRepository.findAllByIds(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
            log.info("Администратору выдан список пользователей");
            return dtos;
        }
    }

    /**
     * Создание пользователя
     *
     * @param newUserRequest NewUserRequest
     * @return UserDto
     */
    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = new User(0, newUserRequest.getName(), newUserRequest.getEmail());
        UserDto dto = UserMapper.toUserDto(userRepository.save(user));
        log.info("Пользователь с id={} создан", dto.getId());
        return dto;
    }

    /**
     * Удаление пользователя
     *
     * @param userId Long
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userValidation(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id={} создан", userId);
    }

    /**
     * Проверка наличия пользователя в базе по id
     *
     * @param id Long
     */
    private void userValidation(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = '%s' не найден", id));
        }
    }

    /**
     * выдача сущности "Пользователя" по id. Используется в сервисах
     *
     * @param id Long
     * @return User
     */
    @Override
    public User getUserById(Long id) {
        userValidation(id);
        return userRepository.findById(id).get();
    }
}
