package ru.yandex.practicum.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StateInvalidException extends RuntimeException {
    public StateInvalidException(String message) {
        super("This state is not correct");
    }
}
