package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("genres")
public class GenreController {
    private final GenreService genreService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Выполнен запрос /get на получение списка Genre");
        return genreService.findAll();
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable("genreId") Integer genreId) {
        log.info("Выполнен запрос /get на получение Genre по id");
        return genreService.findGenreById(genreId);
    }
}
