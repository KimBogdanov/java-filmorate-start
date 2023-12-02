package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();
    private Long counter = 1L;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(counter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isExist(film.getId())) {
            throw new EntityNotFoundException("Не найден film c id " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        if (!isExist(id)) {
            throw new EntityNotFoundException("Не найден film c id " + id);
        }
        return films.get(id);
    }

    @Override
    public boolean isExist(Long id) {
        Film filmById = films.get(id);
        if (filmById == null) {
            throw new EntityNotFoundException("Не найден film c id " + id);
        }
        return films.containsKey(id);
    }
}