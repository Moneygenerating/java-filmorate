package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film getFilm(int filmId);

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int filmId);
}
