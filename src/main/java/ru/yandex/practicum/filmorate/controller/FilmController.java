package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private Long counter = 0L;
    Map<Long, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получаем фильмы");
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films/{id}")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Create film");
        film.setId(counter++);
        return films.put(film.getId(), film);
    }

    @PutMapping("/films/{id}")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update film");
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new FilmNotExistException("PutMapping, в базе нет такого фильма");
        }
        return film;
    }
}
