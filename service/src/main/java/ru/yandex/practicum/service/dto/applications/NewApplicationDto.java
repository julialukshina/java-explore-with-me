package ru.yandex.practicum.service.dto.applications;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NewApplicationDto {
    @NotNull
    @NotBlank
    private String text;
    @NotBlank
    @NotNull
    private String appReason;
}
