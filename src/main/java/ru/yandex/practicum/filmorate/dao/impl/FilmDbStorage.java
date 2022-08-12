package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        final String sqlQuery = "SELECT FILM_ID,FILMS_NAME," +
                "DESCRIPTION,DURATION,RELEASE_DATE,mpa.MPA_ID,mpa.MPA_RATE FROM FILMS AS f " +
                " JOIN FILM_RATING_MPA as mpa ON f.RATING_MPA = mpa.MPA_ID";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm);
        return films;
    }

    @Override
    public Film getFilm(int filmId) {
        final String sqlQuery = "SELECT FILM_ID,FILMS_NAME," +
                "DESCRIPTION,DURATION,RELEASE_DATE,mpa.MPA_ID,mpa.MPA_RATE FROM FILMS AS f" +
                " JOIN FILM_RATING_MPA as mpa ON f.RATING_MPA = mpa.MPA_ID WHERE FILM_ID=?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, filmId);
        //задать через сеттер жанры после того как создал фильм
        if (films.size() != 1) {
            return null;
        }
        return films.get(0);
    }

    public Set<Film> getFilmsById(Set<Integer> id) {
        String sqlQuery = "SELECT FILM_ID,FILMS_NAME," +
                "DESCRIPTION,DURATION,RELEASE_DATE,mpa.MPA_ID,mpa.MPA_RATE FROM FILMS AS f" +
                " JOIN FILM_RATING_MPA as mpa ON f.RATING_MPA = mpa.MPA_ID WHERE FILM_ID=?";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, id);

        LinkedHashSet<Film> fd = new LinkedHashSet<>(films);

        return fd;

    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film((rs.getInt("FILM_ID")),
                rs.getString("FILMS_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getInt("DURATION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                new Mpa(rs.getInt("FILM_RATING_MPA.MPA_ID"),
                        rs.getString("FILM_RATING_MPA.MPA_RATE")));

    }

    @Override
    public Film saveFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS (FILMS_NAME,DESCRIPTION,DURATION,RATING_MPA,RELEASE_DATE) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setInt(4, film.getRatingMpa().getId());
            final LocalDate release = film.getReleaseDate();
            if (release == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(release));
            }
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET FILMS_NAME = ?,DESCRIPTION = ?,DURATION =? ,RATING_MPA=?,RELEASE_DATE =? WHERE FILM_ID=?";

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , film.getRatingMpa().getId()
                , film.getReleaseDate()
                , film.getId());

        return film;

    }

    @Override
    public Set<Film> getTopFilms(int id) {
        String sqlQuery = "SELECT f.FILM_ID,f.FILMS_NAME,f.DESCRIPTION,f.DURATION,f.RELEASE_DATE,mpa.MPA_ID,mpa.MPA_RATE" +
                " FROM FILMS AS f JOIN FILM_RATING_MPA as mpa ON f.RATING_MPA = mpa.MPA_ID  left join FILM_LIKES as fl " +
                "ON f.FILM_ID = fl.FILM_ID GROUP BY f.FILM_ID ORDER BY COUNT(fl.FILM_ID) DESC LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, id);

        LinkedHashSet<Film> fd = new LinkedHashSet<>(films);

        return fd;
    }

    @Override
    public void deleteFilm(int filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID= ?";
        jdbcTemplate.update(sqlQuery, filmId);

    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void deleteLike(int filmId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID= ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
