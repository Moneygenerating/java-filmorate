package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Stream;

@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

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
        return inMemoryFilmStorage.getFilm(id);
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

    public void addLike(int id, int userId) {
        if (inMemoryFilmStorage.getFilm(id) == null || inMemoryUserStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, поставить лайк не получилось");
        }

        Film film = inMemoryFilmStorage.getFilm(id);
        User user = inMemoryUserStorage.getUser(userId);

        inMemoryFilmStorage.addLike(film, user);
    }

    public void deleteLike(int id, int userId) {
        if (inMemoryFilmStorage.getFilm(id) == null || inMemoryUserStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, удалить лайк не получилось");
        }

        Film film = inMemoryFilmStorage.getFilm(id);
        User user = inMemoryUserStorage.getUser(userId);

        inMemoryFilmStorage.deleteLike(film, user);
    }

    public Stream<Film> findFilmByCount(Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }

        return inMemoryFilmStorage.getFilms().values().stream()
                .sorted((f0, f1) -> {
                    int comp = f0.getUserId().size() - f1.getUserId().size();
                    return comp * -1;
                })
                .limit(count);
    }
}
