package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AppStatusConverter implements Converter<String, AppStatus> {
    @Override
    public AppStatus convert(String source) {
        try {
            return source.isEmpty() ? null : AppStatus.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return AppStatus.UNSUPPORTED_STATUS;
        }
    }
}
