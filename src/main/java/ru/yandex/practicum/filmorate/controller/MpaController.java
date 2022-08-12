package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("mpa")
public class MpaController {
    private final MpaService mpaService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public MpaController(MpaService mpaService){
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> findAll(){
        log.info("Выполнен запрос /get на получение списка Mpa");
        return mpaService.findAll();
    }

    //получение mpa по id
    @GetMapping("/{mpaId}")
    public Mpa getMpa(@PathVariable("mpaId") Integer mpaId) {
        log.info("Выполнен запрос /get на получение фильма по id");
        return mpaService.findMpaById(mpaId);
    }
}
