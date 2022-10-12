package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AppReasonConverter implements Converter<String, AppReason> {
    @Override
    public AppReason convert(String source) {
        try {
            return source.isEmpty() ? null : AppReason.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return AppReason.UNSUPPORTED_REASON;
        }
    }
}
