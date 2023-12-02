package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

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
        getFilmIfExist(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilmIfExist(Long id) {
        Film filmById = filmStorage.getFilmById(id);
        if (filmById == null) {
            throw new EntityNotFoundException("Не найден film c id {} " + id);
        }
        return filmById;
    }

    public Film addLike(Long id, Long userId) {
        Film film = getFilmIfExist(id);
        userService.getUserIfExist(userId);
        film.addLike(userId);
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(Long id, Long userId) {
        Film film = getFilmIfExist(id);
        userService.getUserIfExist(userId);
        film.deleteLike(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return getFilms().stream()
                .sorted((film1, film2) -> {
                    return film2.getLikes().size() - film1.getLikes().size();
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}