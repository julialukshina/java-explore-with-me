package ru.yandex.practicum.service.dto.users;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    @NotNull
    @NotBlank
    private long id;
    @Email
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String name;
}
