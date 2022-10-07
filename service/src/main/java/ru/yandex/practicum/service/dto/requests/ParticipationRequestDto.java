package ru.yandex.practicum.service.dto.requests;

import lombok.*;

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
