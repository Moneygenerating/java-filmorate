package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        //final Film newFilm = filmDbStorage.saveFilm(film);
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
        // добавить поменять на эту реализацию List<Film> films = filmDbStorage.getFilms().values();
        List<Film> films = (List<Film>) filmDbStorage.getFilms();

        genreDbStorage.loadFilmGenre(films);
        likeDbStorage.loadFilmLikes(films);

        for (Film film : films) {
            genreDbStorage.loadFilmGenre(film);
        }
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
    //toDO
    public void addLike(int id, int userId) {
        if (filmDbStorage.getFilm(id) == null || userDbStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, поставить лайк не получилось");
        }

        Film film = filmDbStorage.getFilm(id);
        User user = userDbStorage.getUser(userId);
        filmDbStorage.addLike(film, user);
    }

    public void deleteLike(int id, int userId) {
        if (filmDbStorage.getFilm(id) == null || userDbStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователи/фильмы с такими id не найдены, удалить лайк не получилось");
        }

        Film film = filmDbStorage.getFilm(id);
        User user = userDbStorage.getUser(userId);

        filmDbStorage.deleteLike(film, user);
    }

    public Stream<Film> findFilmByCount(Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }

        return filmDbStorage.getFilms().stream()
                .sorted((f0, f1) -> {
                    int comp = f0.getUserId().size() - f1.getUserId().size();
                    return comp * -1;
                })
                .limit(count);
    }
}
