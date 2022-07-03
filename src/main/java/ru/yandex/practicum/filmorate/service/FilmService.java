package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDescriptionException;
import ru.yandex.practicum.filmorate.exception.FilmExceptions;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmId = 0;

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film createFilm(Film film) {
        checkDescription(film);
        validate(film);
        if (films.containsKey(film.getId())) {
            throw new FilmExceptions(String.format(
                    "Фильм с таким названием %s уже существует.",
                    film.getName()
            ));
        }
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkDescription(film);
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new FilmExceptions(String.format(
                    "Фильм с id %s не найден.",
                    film.getId()
            ));
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film findFilmById(Integer id) {
        if (id == null || films.get(id) == null) {
            throw new FilmNotFoundException("Фильм с таким id не найден.");
        }
        return films.get(id);
    }

    void checkDescription(Film film) {
        if (film.getDescription().length() > 200) {
            throw new FilmDescriptionException("Количество символов не может быть больше 200.");
        }
    }

    void validate(Film film) {
        LocalDate dateRelease = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(dateRelease)) {
            throw new FilmExceptions("Некорректная дата выпуска фильма");
        }
    }
}