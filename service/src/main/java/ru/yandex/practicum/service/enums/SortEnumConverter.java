package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SortEnumConverter implements Converter<String, Sort> {
    @Override
    public Sort convert(String source) {
        try {
            return source.isEmpty() ? null : Sort.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return Sort.UNSUPPORTED_SORT;
        }
    }
}
