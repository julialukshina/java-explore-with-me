package ru.yandex.practicum.service.exeptions;

public class MyValidationException extends RuntimeException {
    public MyValidationException(String message) {
        super(message);
    }
}
