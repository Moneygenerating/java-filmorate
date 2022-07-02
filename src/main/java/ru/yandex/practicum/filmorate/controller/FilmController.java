package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    //добавление фильма
    @PostMapping
    public Film createFilm (@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    //обновление фильма
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    //получение фильма по имени
    @GetMapping("/film/{filmId}")
    public Film getFilm(@PathVariable("filmId") Integer filmId){
        return filmService.findFilmByName(filmId);
    }
}