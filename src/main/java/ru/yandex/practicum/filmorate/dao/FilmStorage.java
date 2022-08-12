package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> getFilms();

    Set<Film> getFilmsById(Set<Integer> id);

    Set<Film> getTopFilms(int id);

    Film getFilm(int filmId);

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int filmId);

    void addLike(Integer id, Integer userId);

    void deleteLike(int filmId);
}
