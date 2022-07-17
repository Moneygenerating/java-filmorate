package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;
    FilmService filmService;
    Film film;
    Film film2;

    @BeforeEach
    void init() {
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryFilmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);

        film = new Film()
                .setId(1)
                .setDescription("Фильм о жизни")
                .setName("Достучаться до небес")
                .setReleaseDate(LocalDate.of(1997, Month.APRIL, 20))
                .setDuration(82);

        film2 = new Film()
                .setId(2)
                .setDescription("Фильм о жизни")
                .setName("Карты, деньги, два ствола")
                .setReleaseDate(LocalDate.of(1998, Month.APRIL, 20))
                .setDuration(107);
    }

    @Test
    void findAll() {
        filmService.createFilm(film);
        filmService.createFilm(film2);
        assertEquals(2, filmService.findAll().size());
    }

    @Test
    void validateError() {
        film.setReleaseDate(LocalDate.of(1810, Month.APRIL, 20));
        assertThrows(FilmExceptions.class, () -> filmService.validate(film));
    }

    @Test
    void checkDescriptionError() {
        film.setDescription("fsdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "fffffffffffffffffffffffffffffffffffffffffffluuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu" +
                "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "dfsfsdfdsfsdfsdfsdfsdfsdfsdfsdfsdfsdfsfsdfsdfsdfsdfdsfsdfsdfsdfsdfsdfsdfsdfsdf");

        assertThrows(FilmDescriptionException.class, () -> filmService.checkDescription(film));
    }

    @Test
    void findFilmByIdError() {
        assertThrows(FilmNotFoundException.class, () -> filmService.findFilmById(2342342));
    }
}