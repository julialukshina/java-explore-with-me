package ru.yandex.practicum.service.dto.requests;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private String created;
    private long event;
    private long requester;
    private String status;
}
