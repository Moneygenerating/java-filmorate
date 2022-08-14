package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Set;

public interface MpaStorage {

    void loadFilmMpa(Film film);

    Set<Mpa> getMpaAll();

    Mpa getMpaById(Integer mpaId);

}
