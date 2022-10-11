package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StatusEnumConverter implements Converter<String, Status> {
    @Override
    public Status convert(String source) {
        try {
            return source.isEmpty() ? null : Status.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return Status.UNSUPPORTED_STATUS;
        }
    }
}
