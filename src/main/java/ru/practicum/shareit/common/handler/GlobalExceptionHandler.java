package ru.practicum.shareit.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(RuntimeException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleNotSupportedMethod(HttpRequestMethodNotSupportedException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(ObjectNotFoundException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorResponse handleGeneralExceptions(Exception ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }
}