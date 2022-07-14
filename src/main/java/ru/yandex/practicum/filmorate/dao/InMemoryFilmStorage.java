package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Component
@Getter
@NoArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    protected int filmId = 0;

    protected HashMap<Integer, Film> films = new HashMap<>();

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
}
