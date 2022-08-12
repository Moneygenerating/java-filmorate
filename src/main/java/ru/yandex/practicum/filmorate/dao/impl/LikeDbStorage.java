package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
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

    @Override
    public void setFilmLikesByUser(User user) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID= ?";

        if (user.getUserLikes() == null || user.getUserLikes().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, user.getId());
        }

        for (Likes likes : user.getUserLikes()) {
            String sqlQueryGenre = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";
            jdbcTemplate.update(sqlQueryGenre, likes.getUserId(), likes.getFilmId());
        }
    }


    //загрузка фильмов пролайканных пользователем
    @Override
    public void loadFilmLikes(Film film) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE FILM_ID= ?";
        List<Likes> likes = jdbcTemplate.query(sqlQuery, LikeDbStorage::makeLike, film.getId());
        LinkedHashSet<Likes> likeSet = new LinkedHashSet<>(likes);
        if (likeSet.size() != 0) {
            film.setLikes(likeSet);
        }
    }

    @Override
    public void loadFilmLikes(List<Film> films) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE FILM_ID= ?";
        final Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        for (Integer id : filmMap.keySet()) {
            List<Likes> likes = jdbcTemplate.query(sqlQuery, LikeDbStorage::makeLike, id);
            LinkedHashSet<Likes> likeSet = new LinkedHashSet<>(likes);
            if (likeSet.size() != 0) {
                filmMap.get(id).setLikes(likeSet);
            }
        }
    }

    @Override
    public void loadFilmLikesByUser(List<User> users) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE USER_ID= ?";
        final Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        for (Integer id : userMap.keySet()) {
            List<Likes> likes = jdbcTemplate.query(sqlQuery, LikeDbStorage::makeLike, id);
            LinkedHashSet<Likes> likeSet = new LinkedHashSet<>(likes);
            if (likeSet.size() != 0) {
                userMap.get(id).setUserLikes(likeSet);
            }
        }
    }

    @Override
    public void loadFilmLikesByUser(User user) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE USER_ID= ?";
        List<Likes> likes = jdbcTemplate.query(sqlQuery, LikeDbStorage::makeLike, user.getId());
        LinkedHashSet<Likes> likeSet = new LinkedHashSet<>(likes);
        if (likeSet.size() != 0) {
            user.setUserLikes(likeSet);
        }
    }

    @Override
    public void deleteFilmLikes(Film film) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID= ?";

        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, film.getId());
        }
    }

    @Override
    public void deleteFilmLikesByUser(User user) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID= ?";

        if (user.getUserLikes() == null || user.getUserLikes().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, user.getId());
        }
    }

    /*
    @Override
    public Set<Integer> getTopFilmsByParams(int count){
        String sqlQuery = "SELECT FILM_ID, COUNT(FILM_ID) FROM FILM_LIKES GROUP BY FILM_ID ORDER BY COUNT(FILM_ID) DESC";
        List<Integer> likes = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        LinkedHashSet<Integer>id = new LinkedHashSet<>(likes);
        return id;
    }

     */

    static Likes makeLike(ResultSet rs, int rowNum) throws SQLException {
        return new Likes((rs.getInt("USER_ID")),
                rs.getInt("FILM_ID"));
    }
}
