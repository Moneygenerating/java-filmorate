package ru.yandex.practicum.filmorate.dao.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    protected int filmId = 0;
    protected Map<Integer, Film> films = new HashMap<>();

    @Override
    public HashMap<Integer, Film> getFilms() {
        return (HashMap<Integer, Film>) films;
    }

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film saveFilm(Film film) {
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        films.remove(filmId);
    }

    @Override
    public void addLike(Film film, User user) {
        film.getUserId().add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.getUserId().remove(user.getId());
    }
}
