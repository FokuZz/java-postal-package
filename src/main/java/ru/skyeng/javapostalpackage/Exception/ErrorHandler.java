package ru.skyeng.javapostalpackage.Exception;

import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.skyeng.javapostalpackage.Exception.model.ErrorResponse;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleError(final ValidationException e) {
        return new ErrorResponse("VALIDATION: " + e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleError(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleError(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleError(final HttpMessageNotReadableException e) {
        if (e.getMessage().contains("Required request body is missing")) {
            return new ErrorResponse("Отсутствует тело запроса");
        }
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleError(final MethodArgumentNotValidException e) {

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleError(final EmptyResultDataAccessException e) {
        return new ErrorResponse("В базе данных нет записи с таким идентификатором");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleError(final MissingRequestHeaderException e) {
        return new ErrorResponse("Отсутствует необходимое значение в header");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleError(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse("Такой страницы не существует");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleError(final DataIntegrityViolationException e) {
        if (e.getMessage().contains("Нарушение уникального индекса или первичного ключа")) {
            return new ErrorResponse("Такой объект уже существует");
        }
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleError(final Exception e) {
        return new ErrorResponse("ERROR: Необработанная ошибка -- " + e.getClass() + " -- " + e.getMessage());
    }

}
