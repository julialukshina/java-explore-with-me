package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StateEnumConverter implements Converter<String, State> {
    @Override
    public State convert(String source) {
        try {
            return source.isEmpty() ? null : State.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return State.UNSUPPORTED_STATE;
        }
    }
}