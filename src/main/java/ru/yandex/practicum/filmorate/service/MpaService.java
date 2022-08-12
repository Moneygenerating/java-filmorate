package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Set;

@Service
public class MpaService {
    final MpaStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Mpa> findAll() {
        Set<Mpa> mpaSet = mpaDbStorage.getMpaAll();
        return mpaSet;
    }

    public Mpa findMpaById(Integer mpaId) {

        if (mpaId < 0 || mpaDbStorage.getMpaById(mpaId) == null) {
            throw new MpaNotFoundException(String.format(
                    "Жанр mpa с id %s не найден.",
                    mpaId
            ));
        }

        final Mpa mpaById = mpaDbStorage.getMpaById(mpaId);
        return mpaById;
    }
}
