package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilm(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(Long id, Long userId) {
        Film film = getFilm(id);
        userService.getUserById(userId);
        film.addLike(userId);
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(Long id, Long userId) {
        Film film = getFilm(id);
        userService.getUserById(userId);
        film.deleteLike(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms(count);
    }
}