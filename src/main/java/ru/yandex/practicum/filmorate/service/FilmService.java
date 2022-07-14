package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
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
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getFilms().values();
    }

    public Film createFilm(Film film) {
        checkDescription(film);
        validate(film);
        if (inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            throw new FilmExceptions(String.format(
                    "Фильм с таким названием %s уже существует.",
                    film.getName()
            ));
        }
        return inMemoryFilmStorage.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        checkDescription(film);
        validate(film);
        if (!inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    film.getId()
            ));
        }
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film findFilmById(Integer id) {
        if (id == null || inMemoryFilmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    id
            ));
        }
        return inMemoryFilmStorage.getFilm(id) ;
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
