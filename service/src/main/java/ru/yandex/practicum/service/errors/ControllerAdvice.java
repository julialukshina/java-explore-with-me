package ru.yandex.practicum.service.errors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        ApiError apiError = new ApiError("could not execute statement; SQL [n/a]; constraint [uq_category_name]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement",
                "Error occurred");
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MyNotFoundException.class)
    public ResponseEntity<Object> handleEventNotFoundException(MyNotFoundException e, WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setReason("The required object was not found.");
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MyValidationException.class)
    public ResponseEntity<Object> handleEventValidationException(MyValidationException e, WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setErrors(new ArrayList<>());
//        apiError.setMessage("Only pending or canceled events can be changed");
        apiError.setMessage(e.getMessage());
        apiError.setReason("For the requested operation the conditions are not met.");
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<String> handleConstraintViolationException(IllegalArgumentException e) {
//        return new ResponseEntity<>("{\"error\": \"Unknown state: UNSUPPORTED_STATUS\"}",
//                HttpStatus.BAD_REQUEST);
//    }
//    @ExceptionHandler({MyEntityNotFoundException.class, EntityNotFoundException.class})
//    protected ResponseEntity<Object> handleEntityNotFoundEx(MyEntityNotFoundException ex, WebRequest request) {
//        ApiError apiError = new ApiError("Entity Not Found Exception", ex.getMessage());
//        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = new ApiError(errors, "For the requested operation the conditions are not met.",
                "Only pending or canceled events can be changed");
        return new ResponseEntity<>(apiError, status);
    }
}
