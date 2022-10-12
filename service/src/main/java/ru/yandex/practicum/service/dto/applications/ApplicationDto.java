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
public class ApplicationDto {
    private long id;
    @NotNull
    @NotBlank
    private String text;
    @NotNull
    @NotBlank
    private String authorName;
    private String created;
    private String appStatus;
    private String appReason;
}
