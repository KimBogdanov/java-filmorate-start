package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
        if(isExist(film.getId())){
            throw new EntityNotFoundException("Фильма с id " + film.getId() + " нет в базе");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        if(isExist(id)){
            throw new EntityNotFoundException("Фильма с id " + id + " нет в базе");
        }

        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        if(isExist(id)){
            throw new EntityNotFoundException("Фильма с id " + id + " нет в базе");
        }
        if(userService.isExist(userId)){
            throw new EntityNotFoundException("User с id " + userId + " нет в базе");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        if(isExist(id)){
            throw new EntityNotFoundException("Фильма с id " + id + " нет в базе");
        }
        if(userService.isExist(userId)){
            throw new EntityNotFoundException("User с id " + userId + " нет в базе");
        }
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
    public boolean isExist(Long id){
        return filmStorage.isExist(id);
    }
}