package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmDbStorage filmDbStorage;
    JdbcTemplate jdbcTemplate;
    UserDbStorage userDbStorage;
    FilmService filmService;
    FilmController filmController;
    Film film;
    Film film2;

    @BeforeEach
    void init() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        //filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage) toDO;
        filmController = new FilmController(filmService);
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