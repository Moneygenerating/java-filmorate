package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    void setFilmGenre(Film film);


    void loadFilmGenre(Film film);

    void deleteFilmGenre(Film film);

    void loadFilmGenre(List<Film> films);

    Set<Genre> getGenreAll();

    Genre getGenreById(Integer genreId);

}
