package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class MpaDbStorage implements MpaStorage {

    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void loadFilmMpa(Film film) {
        String sqlQuery = "SELECT f.FILMS_ID, m.MPA_RATE FROM FILMS AS f JOIN FILM_RATING_MPA AS m ON " +
                "f.RATING_MPA = m.MPA_ID WHERE FILMS_ID= ?";

        Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::makeMpa, film.getId());
        //обновляем жанры
        film.setRatingMpa(mpa);

    }

    static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("FILM_ID"),
                rs.getString("FILM_RATING_MPA.MPA_RATE"));

    }
}
