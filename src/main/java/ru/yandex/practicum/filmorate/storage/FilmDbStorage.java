package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public class FilmDbStorage implements FilmStorage {
    @Override
    public List<Film> getPopularFilms() {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilmById(Long id) {
        return null;
    }

    @Override
    public boolean isExist(Long id) {
        return false;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }
}
