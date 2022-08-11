package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class LikeDbStorage implements LikeStorage {
    JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFilmLikeByUserId(int userId, int filmId) {
        String sqlQueryGenre = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";
        jdbcTemplate.update(sqlQueryGenre, userId, filmId);
    }

    @Override
    public void setFilmLikes(Film film) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID= ?";

        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, film.getId());
        }

        for (Likes likes : film.getLikes()) {
            String sqlQueryGenre = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";
            jdbcTemplate.update(sqlQueryGenre, likes.getUserId(), likes.getFilmId());
        }
    }

    //загрузка фильмов пролайканных пользователем
    @Override
    public void loadFilmLikes(Film film) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE FILM_ID= ?";

        Set<Likes> likes = (Set<Likes>) jdbcTemplate.query(sqlQuery, LikeDbStorage::makeLike, film.getId());
        //обновляем жанры
        film.setLikes(likes);
    }

    @Override
    public void deleteFilmLikes(Film film){
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID= ?";

        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, film.getId());
        }
    }


    static Likes makeLike(ResultSet rs, int rowNum) throws SQLException {
        return new Likes((rs.getInt("USER_ID")),
                rs.getInt("FILM_ID"));
    }
}
