package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        log.info("404 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        log.info("404 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserExist(final UserAlreadyExistException e) {
        log.info("409 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailError(final InvalidEmailException e) {
        log.info("400 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserBirthdayError(final UserBirthdayException e) {
        log.info("400 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleFilmExceptions(final FilmExceptions e) {
        log.info("409 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleFilmDescribeExceptions(final FilmDescriptionException e) {
        log.info("409 {}",e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterError(final IncorrectParameterException e) {
        log.info("400 {}",e.getMessage());
        return new ErrorResponse(
                String.format("Ошибка с полем \"%s\".", e.getParameter())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherException(final Throwable e) {
        log.info("500 {}",e.getMessage());
        return new ErrorResponse(
                "Произошла непредвиденная ошибка"
        );
    }
}