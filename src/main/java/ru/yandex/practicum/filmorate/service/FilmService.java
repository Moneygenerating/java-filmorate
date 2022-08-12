package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilmService {

    final FilmStorage filmDbStorage;
    final GenreStorage genreDbStorage;
    final LikeStorage likeDbStorage;
    final MpaStorage mpaStorage;
    final UserStorage userDbStorage;

    @Autowired
    public FilmService(FilmStorage filmDbStorage, UserStorage userDbStorage, GenreStorage genreDbStorage,
                       LikeStorage likeDbStorage, MpaStorage mpaStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.mpaStorage = mpaStorage;
    }


    public Film createFilm(Film film) {
        checkDescription(film);
        validate(film);
        List<Integer> filmsId = filmDbStorage.getFilms().stream().map(Film::getId).collect(Collectors.toList());
        if (filmsId.contains(film.getId())) {
            throw new FilmExceptions(String.format(
                    "Фильм с таким названием %s уже существует.",
                    film.getName()
            ));
        }

        final Film newFilm = filmDbStorage.saveFilm(film);

        // для List newFilm(когда передали много фильмов)
        // genreDbStorage.setFilmGenre(Collections.singletonList(newFilm));

        genreDbStorage.setFilmGenre(film);
        genreDbStorage.loadFilmGenre(newFilm);

        if (film.getLikes() != null) {
            likeDbStorage.setFilmLikes(film);
        }
        return newFilm;
    }

    public Film updateFilm(Film film) {
        checkDescription(film);
        validate(film);
        List<Integer> filmsId = filmDbStorage.getFilms().stream().map(Film::getId).collect(Collectors.toList());
        if (!filmsId.contains(film.getId())) {
            throw new FilmNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    film.getId()
            ));
        }
        //удаляем по фильму связи лайков и жанров
        genreDbStorage.deleteFilmGenre(film);
        likeDbStorage.deleteFilmLikes(film);

        //сохраняем в бд новый фильм
        final Film newFilm = filmDbStorage.updateFilm(film);

        //обновляем связи лайков и жанров
        genreDbStorage.setFilmGenre(newFilm);

        if (film.getLikes() != null) {
            likeDbStorage.setFilmLikes(newFilm);
        }
        return newFilm;
    }

    public Film findFilmById(Integer id) {
        final Film film = filmDbStorage.getFilm(id);
        if (id == null || film == null) {
            throw new FilmNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    id
            ));
        }
        genreDbStorage.loadFilmGenre(Collections.singletonList(film));
        return film;
    }


    public Collection<Film> findAll() {
        List<Film> films = filmDbStorage.getFilms();

        genreDbStorage.loadFilmGenre(films);
        likeDbStorage.loadFilmLikes(films);

        return films;
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
        if (filmDbStorage.getFilm(id) == null || userDbStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, поставить лайк не получилось");
        }
        filmDbStorage.addLike(id, userId);

    }

    public void deleteLike(int filmId, int userId) {
        if (filmDbStorage.getFilm(filmId) == null || userDbStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, удалить лайк не получилось");
        }

        filmDbStorage.deleteLike(filmId);
    }

    public Stream<Film> findFilmByCount(Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }

        Set<Film> films = filmDbStorage.getTopFilms(count);
        return films.stream();
    }
}
