package ru.yandex.practicum.statistics.exceptions;

public class MyValidationException extends RuntimeException {
    public MyValidationException(String message) {
        super(message);
    }
}