package ru.yandex.practicum.service.dto.applications;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    private long id;
    private String text;
    private long authorId;
    private String created;
    private String appStatus;
    private String appReason;
}
