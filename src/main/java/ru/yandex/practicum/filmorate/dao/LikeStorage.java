package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikeStorage {
    void setFilmLikes(Film film);

    void loadFilmLikes(Film film);
}
