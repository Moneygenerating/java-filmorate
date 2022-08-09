package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HashMap<Integer, Film> getFilms(){
        return null;
    }

    @Override
    public Film getFilm(int filmId){
        final String sqlQuery = "SELECT FILM_ID,FILMS_NAME," +
                "DESCRIPTION,DURATION,RELEASE_DATE FROM FILMS WHERE FILM_ID=?";
        final List<Film> films = jdbcTemplate.query(sqlQuery,FilmDbStorage::makeFilm,filmId);
        //задать через сеттер жанры после того как создал фильм
        if(films.size()!=1){
            return null;
        }
        return films.get(0);
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
    public Film saveFilm(Film film){
        String sqlQuery = "INSERT INTO FILMS (FILMS_NAME,DESCRIPTION,DURATION,RELEASE_DATE) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            final LocalDate release = film.getReleaseDate();
            if (release == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(release));
            }
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film){
        return null;
    }

    @Override
    public void deleteFilm( int filmId){
    }

    @Override
    public void addLike(Film film, User user){

    }

    @Override
    public void deleteLike(Film film, User user){

    }
}
