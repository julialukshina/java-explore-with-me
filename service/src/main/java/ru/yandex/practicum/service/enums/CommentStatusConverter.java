package ru.yandex.practicum.service.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CommentStatusConverter implements Converter<String, CommentStatus> {
    @Override
    public CommentStatus convert(String source) {
        try {
            return source.isEmpty() ? null : CommentStatus.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return CommentStatus.UNSUPPORTED_STATUS;
        }
    }
}
