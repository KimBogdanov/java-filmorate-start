package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> getFilms() {
        log.info("getFilms {}");
        return filmService.getFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilmByIdCheck(id);
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("createFilm {}" + film.getId());
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("updateFilm {}" + film.getId());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("addLike {} user {} " + id + userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("deleteLike {} user {} " + id + userId);
        return filmService.deleteLike(id, userId);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}