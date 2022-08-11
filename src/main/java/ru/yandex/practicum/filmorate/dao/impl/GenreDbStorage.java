package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        String sqlQuery = "SELECT fgi.FILMS_ID, g.FILM_GENRE FROM FILMS_GENRES_IDS AS fgi JOIN FILM_GENRES AS g ON " +
                "fgi.FILMS_GENRE_ID = g.GENRES_ID WHERE FILMS_ID= ?";

        Set<Genre> genres = (Set<Genre>) jdbcTemplate.query(sqlQuery, GenreDbStorage::makeGenre, film.getId());
        //обновляем жанры
        film.setGenres(genres);

    }

    @Override
    public void loadFilmGenre(List<Film> films) {
        String sqlQuery = "SELECT fgi.FILMS_ID, g.FILM_GENRE FROM FILMS_GENRES_IDS AS fgi JOIN FILM_GENRES AS g ON " +
                "fgi.FILMS_GENRE_ID = g.GENRES_ID WHERE FILMS_ID= ?";
        final List<Integer> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        final Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        for (Integer id : filmMap.keySet()) {
            Set<Genre> genres = (Set<Genre>) jdbcTemplate.query(sqlQuery, GenreDbStorage::makeGenre, id);
            filmMap.get(id).setGenres(genres);
        }
    }

    @Override
    public void deleteFilmGenre(Film film){
        //todo
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("FILMS_ID"),
                rs.getString("FILMS_GENRES.FILM_GENRE"));

    }
}
