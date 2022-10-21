package ru.yandex.practicum.service.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.service.exeptions.ConflictException;
import ru.yandex.practicum.service.exeptions.NotFoundException;
import ru.yandex.practicum.service.exeptions.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ServiceExceptionHandler{
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("Запрос составлен с ошибкой");
        apiError.setStatus("BAD_REQUEST");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("Произошла ошибка");
        apiError.setStatus("INTERNAL_SERVER_ERROR");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("Запрашиваемый объект не найден");
        apiError.setStatus("NOT_FOUND");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("Не соблюдены условия для запрошенной операции");
        apiError.setStatus("BAD_REQUEST");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("В данных передан null");
        apiError.setStatus("BAD_REQUEST");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException e) {
        log.error(e.getMessage());
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("Конфликт данных");
        apiError.setStatus("CONFLICT");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(apiError.getErrors()::add);
        apiError.setTimestamp(LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
