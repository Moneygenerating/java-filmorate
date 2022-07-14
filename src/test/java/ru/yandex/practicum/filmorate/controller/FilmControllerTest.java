package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;
    FilmController filmController;
    Film film;
    Film film2;

    @BeforeEach
    void init() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(filmService);
        film = new Film(1, "Фильм о жизни", "Достучаться до небес"
                , LocalDate.of(1997, Month.APRIL, 20), 82);

        film2 = new Film(2, "Фильм о жизни", "Карты, деньги, два ствола"
                , LocalDate.of(1998, Month.APRIL, 20), 107);

    }

    @Test
    void findAll() {
        filmController.createFilm(film);
        filmController.createFilm(film2);
        assertEquals(2, filmController.findAll().size());
    }

    @Test
    void createFilm() {
        filmController.createFilm(film);
        assertEquals(film, filmController.getFilm(1));
    }

    @Test
    void updateFilm() {
        filmController.createFilm(film);
        film2.setId(1);
        filmController.updateFilm(film2);
        assertEquals(film2, filmController.getFilm(1));
    }

    @Test
    void getFilm() {
        filmController.createFilm(film);
        filmController.createFilm(film2);

        assertEquals(film, filmController.getFilm(1));
        assertEquals(film2, filmController.getFilm(2));
    }
}