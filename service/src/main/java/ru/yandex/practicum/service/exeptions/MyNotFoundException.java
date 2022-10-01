package ru.yandex.practicum.service.exeptions;

public class MyNotFoundException extends RuntimeException {
    public MyNotFoundException(String message) {
        super(message);
    }
}
