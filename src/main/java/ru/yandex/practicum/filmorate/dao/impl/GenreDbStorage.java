package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM FILMS_GENRES_IDS WHERE FILMS_ID= ?";

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, film.getId());
        }

        for (Genre genre : film.getGenres()) {
            String sqlQueryGenre = "INSERT INTO FILMS_GENRES_IDS (FILMS_ID, FILM_GENRE_ID) VALUES (?,?)";
            jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
        }
    }

    @Override
    public void loadFilmGenre(Film film) {
        String sqlQuery = "SELECT g.GENRES_ID, g.FILM_GENRE FROM FILMS_GENRES_IDS AS fgi LEFT JOIN FILM_GENRES AS g ON " +
                "fgi.FILM_GENRE_ID = g.GENRES_ID WHERE FILMS_ID= ?";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::makeGenre, film.getId());
        LinkedHashSet<Genre> fd = new LinkedHashSet<>(genres);
        //обновляем жанры
        film.setGenres(fd);

    }

    @Override
    public void loadFilmGenre(List<Film> films) {
        String sqlQuery = "SELECT g.GENRES_ID, g.FILM_GENRE FROM FILMS_GENRES_IDS AS fgi LEFT JOIN FILM_GENRES AS g ON " +
                "fgi.FILM_GENRE_ID = g.GENRES_ID WHERE FILMS_ID= ?";
        final List<Integer> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        final Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        for (Integer id : filmMap.keySet()) {
            List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::makeGenre, id);
            LinkedHashSet<Genre> fd = new LinkedHashSet<>(genres);
            if (genres.size() != 0) {
                filmMap.get(id).setGenres(fd);
            }
        }
    }

    @Override
    public Set<Genre> getGenreAll() {
        String sqlQuery = "SELECT * FROM FILM_GENRES";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::makeGenre);
        LinkedHashSet<Genre> genreSet = new LinkedHashSet<>(genres);
        return genreSet;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        String sqlQuery = "SELECT * FROM FILM_GENRES WHERE GENRES_ID = ?";
        Genre genre = jdbcTemplate.queryForObject(sqlQuery, GenreDbStorage::makeGenre, genreId);
        return genre;
    }

    @Override
    public void deleteFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM FILMS_GENRES_IDS WHERE FILMS_ID= ?";
        jdbcTemplate.update(sqlQuery, film.getId());

    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRES_ID"),
                rs.getString("FILM_GENRE"));

    }
}
