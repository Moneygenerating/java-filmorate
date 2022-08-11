package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface GenreStorage {

    void setFilmGenre(Film film);

    void loadFilmGenre(Film film);

    void deleteFilmGenre(Film film);

    void loadFilmGenre(List<Film> films);
}
