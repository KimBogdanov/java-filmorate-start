package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getFilms() {
        return filmStorage.getPopularFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.isExist(film.getId())) {
            throw new EntityNotFoundException("Не найден film c id " + film.getId());
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        if (!filmStorage.isExist(id)) {
            throw new EntityNotFoundException("Не найден film c id " + id);
        }
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
        return filmStorage.getPopularFilms(count);
    }
}