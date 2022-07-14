package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {

    HashMap<Integer, Film> getFilms();

    Film getFilm(int filmId);

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int filmId);
}
