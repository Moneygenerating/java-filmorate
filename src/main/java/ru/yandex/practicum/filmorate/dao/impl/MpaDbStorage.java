package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
public class MpaDbStorage implements MpaStorage {

    private JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void loadFilmMpa(Film film) {
        String sqlQuery = "SELECT f.FILM_ID, m.MPA_RATE FROM FILMS AS f JOIN FILM_RATING_MPA AS m ON " +
                "f.RATING_MPA = m.MPA_ID WHERE FILM_ID= ?";

        Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::makeMpa, film.getId());
        //обновляем MPA
        film.setRatingMpa(mpa);
    }

    @Override
    public Set<Mpa> getMpaAll() {
        String sqlQuery = "SELECT * FROM FILM_RATING_MPA";
        List<Mpa> mpa = jdbcTemplate.query(sqlQuery, MpaDbStorage::makeMpaGenre);
        LinkedHashSet<Mpa> mpaSet = new LinkedHashSet<>(mpa);
        return mpaSet;
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        String sqlQuery = "SELECT * FROM FILM_RATING_MPA WHERE MPA_ID = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::makeMpaGenre, mpaId);
        return mpa;
    }

    static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("FILM_ID"),
                rs.getString("FILM_RATING_MPA.MPA_RATE"));

    }

    static Mpa makeMpaGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"),
                rs.getString("MPA_RATE"));

    }
}
