package ru.yandex.practicum.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TimeValidationException extends RuntimeException {
    public TimeValidationException(String message) {
        super(message);
    }
}
