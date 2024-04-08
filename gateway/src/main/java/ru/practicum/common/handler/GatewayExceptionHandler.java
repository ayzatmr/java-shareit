package ru.practicum.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.common.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class, MissingServletRequestParameterException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintErrors(RuntimeException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        log.info(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleConversionFailedException(MethodArgumentTypeMismatchException ex) {
        log.info(ex.getMessage());
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleNotSupportedMethod(HttpRequestMethodNotSupportedException ex) {
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

    @ExceptionHandler
    public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException e) {
        log.info(e.getResponseBodyAsString());
        return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
    }
}