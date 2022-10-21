package ru.yandex.practicum.service.errors;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String message;
    private String reason;
    private String timestamp;
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors = new ArrayList<>();
}