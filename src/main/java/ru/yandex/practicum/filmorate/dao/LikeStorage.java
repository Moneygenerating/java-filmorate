package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

public interface LikeStorage {
    void setFilmLikes(Film film);

    void loadFilmLikes(Film film);

    void loadFilmLikes(List<Film> films);

    void deleteFilmLikes(Film film);

}
