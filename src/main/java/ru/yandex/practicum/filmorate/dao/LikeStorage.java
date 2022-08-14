package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface LikeStorage {
    void setFilmLikes(Film film);

    void setFilmLikesByUser(User user);

    void loadFilmLikes(Film film);

    void loadFilmLikesByUser(User user);

    void loadFilmLikesByUser(List<User> user);

    void loadFilmLikes(List<Film> films);

    void deleteFilmLikes(Film film);

    void deleteFilmLikesByUser(User user);

}
