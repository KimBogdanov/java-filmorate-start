package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Long counter = 1L;
    Map<Long, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> getFilms() {
        log.info("Получаем фильмы");
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Create film");
        film.setId(counter++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update film");
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new EntityNotExistException("PutMapping, в базе нет такого фильма");
        }
    }
}
