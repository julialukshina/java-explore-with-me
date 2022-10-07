package ru.yandex.practicum.service.errors;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ApiError {
    @JsonIgnore
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String message;
    private String reason;
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)

    private List<String> errors;

    public ApiError(String message, String reason) {
        this.message = message;
        this.reason = reason;
        this.timestamp = LocalDateTime.now().format(formatter);
    }

    public ApiError(List<String> errors, String reason, String message) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.timestamp = LocalDateTime.now().format(formatter);
    }
}