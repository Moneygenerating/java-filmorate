package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

@Service
public class GenreService {
    final GenreStorage genreDbStorage;

    @Autowired
    public GenreService(GenreStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> findAll() {
        Set<Genre> genreSet = genreDbStorage.getGenreAll();
        return genreSet;
    }

    public Genre findGenreById(Integer genreId) {

        if (genreId < 0 || genreDbStorage.getGenreById(genreId) == null) {
            throw new GenreNotFoundException(String.format(
                    "Жанр Genre с id %s не найден.",
                    genreId
            ));
        }

        final Genre genreById = genreDbStorage.getGenreById(genreId);
        return genreById;
    }
}
