package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("films")
public class FilmController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выполнен запрос /get на получение списка фильмов");
        return filmService.findAll();
    }

    //добавление фильма
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Выполнен запрос /post на создание фильма");
        return filmService.createFilm(film);
    }

    //обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Выполнен запрос /put на обновление фильма");
        return filmService.updateFilm(film);
    }

    //получение фильма по имени
    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable("filmId") Integer filmId) {
        log.info("Выполнен запрос /get на получение фильма по id");
        return filmService.findFilmById(filmId);
    }

    //добавление лайка
    @PutMapping("/{id}/like/{userId}")
    public void addFriend(@PathVariable int id, @PathVariable int userId){
        log.info("Выполнен запрос /put на добавление лайка");
        filmService.addLike(id,userId);
    }

    //удаление лайка
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int userId){
        log.info("Выполнен запрос /delete на удаление лайка");
        filmService.deleteLike(id,userId);
    }


    //возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.
    @GetMapping("/popular")
    public Stream<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Выполнен запрос /get на получение списка  фильмов по количеству лайков.");
        return filmService.findFilmByCount(count);
    }
}